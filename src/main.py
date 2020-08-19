#!/usr/bin/env python 
# -*- coding:utf-8 -*-
import vulnweb_wfuzz as vw
import web_scraper as ws
import sys
import getopt

# "http://www.webscantest.com/"
# "http://testphp.vulnweb.com/"
# "http://www.testfire.net/"


def main():
    try:
        opts, args = getopt.getopt(sys.argv[2:], "e")
    except getopt.GetoptError as err:
        sys.stderr.write(err)
        sys.exit(2)

    top_url = sys.argv[1]
    show_error_only = False
    for o, a in opts:
        if o == "-e":
            vw.show_error_only = True

    # web scraper
    s = ws.ScrapedURLs(top_url)
    url_list = s.scrape_all()
    print("\nThe found urls, corresponding url parameters and POST parameters. ")
    for i in url_list:
        print(i.url, i.getparams, i.postparams)

    # perform fuzzing
    com_dir_list = vw.fuzz_common_dir(top_url)
    com_files_list = vw.fuzz_common_files(top_url)

    # print found hidden URLs
    vw.find_hidden_urls(url_list, com_dir_list, com_files_list)

    print("\nPerform Fuzzing on the Found URLs....")
    for url in url_list:
        vw.fuzz_weak_username(url)
        vw.fuzz_sql_injection(url)
        if len(url.getparams) != 0:
            vw.fuzz_xss_injection(url)


def test_main():
    url = ws.URL("http://testphp.vulnweb.com/hpp/")
    url.getparams = ["pp"]
    vw.fuzz_xss_injection(url)


if __name__ == '__main__':
    main()