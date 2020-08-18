import wfuzz
from bs4 import BeautifulSoup
import sys

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
            sys.stdout.write("{0:9d}\t   {1:3d}\t\t    {2} L\t      {3}\t       {4}\t      {5}\t    {6}\t    \n".format(
                clusterid, entry.code, entry.lines, entry.words, entry.chars, str(entry.error_msg), entry.url))
            clusterid += 1


def fuzz_sql_injection(url, parameter, fuzz_result_cluters):
    sys.stdout.write("\nWFuzz -- SQL Injection\n")
    with wfuzz.get_session("-z file,./wfuzz-master/wordlist/Injections/SQL.txt -A {0}?{1}=FUZZ".format(url, parameter)) as s:
        for r in s.fuzz():
            fuzz_result_cluters.append_entry(r)
        fuzz_result_cluters.output()


def fuzz_xss_injection(url, parameter, fuzz_result_cluters):
    sys.stdout.write("\nWFuzz -- XSS Injection\n")
    with wfuzz.get_session("-z file,./wfuzz-master/wordlist/Injections/XSS.txt -A -Z {0}?{1}=FUZZ".format(url, parameter)) as s:
        for r in s.fuzz():
            fuzz_result_cluters.append_entry(r)
        fuzz_result_cluters.output()








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

