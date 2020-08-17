import wfuzz
from bs4 import BeautifulSoup

home_url = "http://testphp.vulnweb.com"


def find_links():
    fh = open('Home of Acunetix Art.html')
    soup = BeautifulSoup(fh, 'html.parser')
    for link in soup.find_all('a'):
        print(link.get('href'))
    return


def find_parameters():
    return


def fuzz_sql_injection(url, parameter):
    with wfuzz.get_session("-z file,./wordlist/Injections/SQL.txt {0}?{1}=FUZZ".format(url, parameter)) as s:
        for r in s.fuzz():
            print(r)


def fuzz_xss_injection(url, parameter):
    with wfuzz.get_session("-z file,./wordlist/Injections/XSS.txt {0}?{1}=FUZZ -A".format(url, parameter)) as s:
        for r in s.fuzz():
            print(r)


def main():
    fuzz_sql_injection(home_url+"/search.php",'test')











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

if __name__ == '__main__':
    main()

