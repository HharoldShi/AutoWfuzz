#!/usr/bin/env python 
# -*- coding:utf-8 -*-
import vulnweb_wfuzz as vw
import web_scraper as ws
import sys
import getopt

# "http://www.webscantest.com/"
# "http://testphp.vulnweb.com/"


def main():
    try:
        opts, args = getopt.getopt(sys.argv[2:], "e")
    except getopt.GetoptError as err:
        sys.stderr.write(err)
        sys.exit(2)

    show_error_only = False
    for o, a in opts:
        if o == "-e":
            vw.show_error_only = True

    top_url = sys.argv[1]
    fuzz_result_cluters = vw.FuzzResultClusters()

    # web scraper
    s = ws.ScrapedURLs(top_url)
    url_list = s.scrape_all()
    print("\nThe found urls, corresponding url parameters and POST parameters. ")
    for i in url_list:
        print(i.url, i.getparams, i.postparams)

    # perform fuzzing
    for url in url_list:
        vw.fuzz_sql_injection(url, fuzz_result_cluters)
        if len(url.getparams) != 0:
            vw.fuzz_xss_injection(url, fuzz_result_cluters)







    # urls = []
    # parameters = []
    # urls.append("/datastore/search_by_id.php") #only int
    # urls.append("/datastore/search_by_name.php") #only strings + two quotes
    # urls.append("/datastore/search_double_by_name.php") # only slashes and two quotes
    # urls.append("/datastore/search_by_statement.php") # search for rake
    # urls.append("/datastore/search_get_by_name.php")
    # urls.append("/datastore/search_single_by_name.php") #only slashes and single quote
    # parameters.append("id")
    # parameters.append("name")

# for url in urls:
#     print("URL Fuzzing: ", url)
#     if "id" in url :
#         vw.fuzz_sql_injection(home_url+url,'id', fuzz_result_cluters)
#         vw.fuzz_xss_injection(home_url+url, 'id', fuzz_result_cluters)
#     else:
#         vw.fuzz_sql_injection(home_url + url, 'name', fuzz_result_cluters)
#         vw.fuzz_xss_injection(home_url + url, 'name', fuzz_result_cluters)


# # fuzzer test
# vw.fuzz_sql_injection(top_url+"search.php",'test', fuzz_result_cluters)
# vw.fuzz_xss_injection(top_url+"search.php",'test', fuzz_result_cluters)



if __name__ == '__main__':
    main()