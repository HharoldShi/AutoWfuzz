import wfuzz
from bs4 import BeautifulSoup
import sys
import random

show_error_only = False
error_msgs = ["Warning: mysql_fetch_array()", "Error 1064", "Error 1054"]


class FuzzResultEntry:
    def __init__(self, code, lines, words, chars, url, error_msg, post_payload):
        self.code = code
        self.lines = lines
        self.words = words
        self.chars = chars
        self.url = url
        self.error_msg = error_msg
        self.post_payload = post_payload

    def __eq__(self, other):
        if isinstance(other, FuzzResultEntry):
            return self.code == other.code and self.lines==other.lines and self.words==other.words and self.chars==other.chars
        return False

    def __hash__(self):
        return hash((self.code, self.lines, self.words, self.chars))


class FuzzResultClusters:
    def __init__(self):
        self.cluster_list = []

    def append_entry(self, r):
        # print((r.history.content).lower() + "\n -----------------------------------------------------------------------------\n")
        error_found = False
        if len(r.plugins['errors']) != 0:
            entry = FuzzResultEntry(r.code, r.lines, r.words, r.chars, r.url, r.plugins['errors'][0], r.history.params.raw_post)
            error_found = True
        else:
            for msg in error_msgs:
                if msg in r.history.content:
                    entry = FuzzResultEntry(r.code, r.lines, r.words, r.chars, r.url, "Error identified: " + msg, r.history.params.raw_post)
                    error_found = True
                    break
        if not error_found:
            entry = FuzzResultEntry(r.code, r.lines, r.words, r.chars, r.url, None, r.history.params.raw_post)
        if entry not in self.cluster_list or entry.error_msg is not None:
            self.cluster_list.append(entry)

    def get_request_output(self):
        if len(self.cluster_list) == 0:
            return
        sys.stdout.write("=======================================================================================================================================\n")
        sys.stdout.write("ClusterID\t Response\t  Lines\t Words\t  Chars\t\tError\t URL\n")
        sys.stdout.write("=======================================================================================================================================\n")
        clusterid = 1
        for entry in self.cluster_list:
            if show_error_only and entry.error_msg is None:
                continue
            sys.stdout.write("{0:9d}\t{1:8d}\t{2:5d} L\t{3:7d}\t {4:7d}\t {5}\t {6}\n".format(
                clusterid, entry.code, entry.lines, entry.words, entry.chars, str(entry.error_msg), entry.url))
            clusterid += 1

    def post_request_output(self):
        if len(self.cluster_list) == 0:
            return
        sys.stdout.write("=======================================================================================================================================\n")
        sys.stdout.write("ClusterID\t Response\t Lines\t  Words\t  Chars\t  Error\t URL\t\t\t\t\t\t Post Payload\n")
        sys.stdout.write("=======================================================================================================================================\n")
        clusterid = 1
        for entry in self.cluster_list:
            if show_error_only and entry.error_msg is None:
                continue
            sys.stdout.write("{0:9d}\t{1:8d}\t{2:5d} L\t{3:7d}\t {4:7d}\t {5}\t {6}\t {7}\n".format(
                clusterid, entry.code, entry.lines, entry.words, entry.chars, str(entry.error_msg), entry.url, entry.post_payload))
            clusterid += 1


def wfuzz_get_request(payload, url, parameters):
    fuzz_result_clusters = FuzzResultClusters()
    str = parameters[0] + "=FUZZ"
    for param in parameters[1:]:
        str += "&" + param + "=FUZZ"
    with wfuzz.get_session("{0} --hc 400,404,-1 -A -Z {1}?{2}".format(payload, url, str)) as s:
        for r in s.fuzz():
            fuzz_result_clusters.append_entry(r)
        fuzz_result_clusters.get_request_output()


def wfuzz_post_request(payload, url, parameters):
    fuzz_result_clusters = FuzzResultClusters()
    post_commands = "-d " + parameters[0] + "=FUZZ"
    for param in parameters[1:]:
        if "submit" in param.lower():
            post_commands += "&" + param + "=Login"
        else:
            post_commands += "&" + param + "=FUZZ"
    with wfuzz.get_session("{0} {1} --hc 400,404,-1 -A -Z {2}".format(payload, post_commands, url)) as s:
        for r in s.fuzz():
            fuzz_result_clusters.append_entry(r)
        fuzz_result_clusters.post_request_output()


def fuzz_sql_injection(url):
    payload = "-z file,../wfuzz-master/wordlist/Injections/SQL.txt"

    if len(url.getparams) != 0:
        sys.stdout.write("\nWFuzz -- SQL Injection on URL parameters, URL = " + url.url + "\n")
        wfuzz_get_request(payload, url.url, url.getparams)

    if len(url.postparams) != 0:
        sys.stdout.write("\nWFuzz -- SQL Injection on POST parameters, URL = " + url.url + "\n")
        wfuzz_post_request(payload, url.url, url.postparams)


def fuzz_xss_injection(url):
    payload = "-z file,../wfuzz-master/wordlist/Injections/XSS.txt"
    if len(url.getparams) != 0:
        sys.stdout.write("\nWFuzz -- XSS Injection on URL parameters, URL = " + url.url + "\n")
        wfuzz_get_request(payload, url.url, url.getparams)

    if len(url.postparams) != 0:
        sys.stdout.write("\nWFuzz -- XSS Injection on POST parameters, URL = " + url.url + "\n")
        wfuzz_post_request(payload, url.url, url.postparams)


def fuzz_weak_username(url):
    if len(url.postparams) >= 2 and "pass" in url.postparams[1]:
        sys.stdout.write("\nWFuzz -- Weak Username and password, URL = " + url.url + "\n")
        payload = "-z file,../wfuzz-master/wordlist/others/common_pass.txt"
        wfuzz_post_request(payload, url.url, url.postparams)


def fuzz_common_dir(url_addr):
    sys.stdout.write("\nWFuzz -- common directories, URL = " + url_addr + "\n")
    com_dir_list = []
    fuzz_result_clusters = FuzzResultClusters()
    payload = "-z file,../wfuzz-master/wordlist/general/common.txt"
    if url_addr[-1] == "/":
        url_addr = url_addr[:-1]
    with wfuzz.get_session("{0} --hc 400,404,-1,403 -A -Z -u {1}/FUZZ".format(payload, url_addr)) as s:
        for r in s.fuzz():
            if (r.code == 301 or r.code == 302) and str(r.history.headers.response.Location)!= "":
                com_dir_list.append(str(r.history.headers.response.Location))
            else:
                com_dir_list.append(r.url)
            fuzz_result_clusters.append_entry(r)
        fuzz_result_clusters.get_request_output()
    return com_dir_list


def fuzz_common_files(url_addr):
    sys.stdout.write("\nWFuzz -- common files, URL = " + url_addr + "\n")
    com_files_list = []
    fuzz_result_clusters = FuzzResultClusters()
    payload = "-z file,../wfuzz-master/wordlist/general/common.txt -z list,php-bak-zip"
    if url_addr[-1] == "/":
        url_addr = url_addr[:-1]
    with wfuzz.get_session("{0} --hc 400,404,-1,403 -A -Z -u {1}/FUZZ.FUZ2Z".format(payload, url_addr)) as s:
        for r in s.fuzz():
            if (r.code == 301 or r.code == 302) and str(r.history.headers.response.Location)!= "":
                com_files_list.append(str(r.history.headers.response.Location))
            else:
                com_files_list.append(r.url)
            fuzz_result_clusters.append_entry(r)
        fuzz_result_clusters.get_request_output()
    return com_files_list


def find_hidden_urls(found_urls, com_dir_list, com_files_list):
    hidden_urls = []
    found_url_list = []
    for url_obj in found_urls:
        found_url_list.append(url_obj.url)

    for url_addr in com_dir_list:
        if url_addr not in found_url_list:
            hidden_urls.append(url_addr)

    for url_addr in com_files_list:
        if url_addr not in found_url_list:
            hidden_urls.append(url_addr)

    if len(hidden_urls) > 0:
        sys.stdout.write("\nFound hidden URLs: \n")
        for i in hidden_urls:
            sys.stdout.write(i + "\n")

