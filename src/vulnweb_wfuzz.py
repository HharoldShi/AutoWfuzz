import wfuzz
from bs4 import BeautifulSoup
import sys

show_error_only = False

class FuzzResultEntry:
    def __init__(self, code, lines, words, chars, url, error_msg):
        self.code = code
        self.lines = lines
        self.words = words
        self.chars = chars
        self.url = url
        self.error_msg = error_msg

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
        if len(r.plugins['errors']) != 0:
            entry = FuzzResultEntry(r.code, r.lines, r.words, r.chars, r.url, error_msg=r.plugins['errors'][0])
        else:
            entry = FuzzResultEntry(r.code, r.lines, r.words, r.chars, r.url, None)
        if entry not in self.cluster_list or entry.error_msg is not None:
            self.cluster_list.append(entry)

    def output(self):
        sys.stdout.write("=======================================================================================================================================\n")
        sys.stdout.write("ClusterID      Response     Lines      \tWords     \tChars      \tError                                                         URL\n")
        sys.stdout.write("=======================================================================================================================================\n")
        clusterid = 1
        for entry in self.cluster_list:
            if show_error_only and entry.error_msg is None:
                continue
            sys.stdout.write("{0:9d}\t   {1:3d}\t\t    {2} L\t      {3}\t       {4}\t      {5}\t    {6}\t    \n".format(
                clusterid, entry.code, entry.lines, entry.words, entry.chars, str(entry.error_msg), entry.url))
            clusterid += 1


def wfuzz_get_request(payload, url, parameters, fuzz_result_clusters):
    str = parameters[0] + "=FUZZ"
    for param in parameters[1:]:
        str += "&" + param + "=FUZZ"
    with wfuzz.get_session("{0} -A -Z {1}?{2}".format(payload, url, str)) as s:
        for r in s.fuzz():
            fuzz_result_clusters.append_entry(r)
        fuzz_result_clusters.output()


def wfuzz_post_request(payload, url, parameters, fuzz_result_clusters):
    post_commands = "-d " + parameters[0] + "=FUZZ"
    for param in parameters[1:]:
        post_commands += "&" + param + "=FUZZ"
    with wfuzz.get_session("{0} {1} -A -Z {2}".format(payload, post_commands, url)) as s:
        for r in s.fuzz():
            fuzz_result_clusters.append_entry(r)
        fuzz_result_clusters.output()


def fuzz_sql_injection(url, fuzz_result_clusters):
    payload = "-z file,../wfuzz-master/wordlist/Injections/SQL.txt"

    if len(url.getparams) != 0:
        sys.stdout.write("\nWFuzz -- SQL Injection on URL parameters\n")
        wfuzz_get_request(payload, url.url, url.getparams, fuzz_result_clusters)

    if len(url.postparams) != 0:
        sys.stdout.write("\nWFuzz -- SQL Injection on POST parameters\n")
        wfuzz_post_request(payload, url.url, url.postparams, fuzz_result_clusters)


def fuzz_xss_injection(url, fuzz_result_clusters):
    sys.stdout.write("\nWFuzz -- XSS Injection\n")
    payload = "-z file,../wfuzz-master/wordlist/Injections/XSS.txt"
    wfuzz_get_request(payload, url.url, url.getparams, fuzz_result_clusters)


# TODO: add "weak password", need to locate login page






# for r in wfuzz.fuzz(url="http://testphp.vulnweb.com/FUZZ", hc=[404], payloads=[("file",dict(fn="wordlist/general/common.txt"))]):
#     print(r)
#     print(r.history.cookies.response)
#     print(r.history.params.all)

# with wfuzz.get_session("-z list --zD test -u http://testphp.vulnweb.com/userinfo.php -d uname=FUZZ&pass=FUZZ") as s:
#     for r in s.fuzz():
#         print(r.history.cookies.response)
#         print(r.history.params.all)
#         print(r.history.params.post)
#         print(r.history.params.post.uname)
#         print(r.history.params.post['pass'])

# with wfuzz.get_session("-z file,./wordlist/general/common.txt -u http://testphp.vulnweb.com/FUZZ") as s:
#     for r in s.fuzz():
#         print(r)
        # print(r.history.cookies)
        # print(r.history.params.location)
        # if r.code == 301:
        #     print(r.history.headers.response.Location)

# with wfuzz.get_session("--script=robots -z list,'robots.txt' http://testphp.vulnweb.com/FUZZ") as s:
#     for r in s.fuzz():
#         print(r)

