#!/usr/bin/env python 
# -*- coding:utf-8 -*-
import vulnweb_wfuzz as vw


fuzz_result_cluters = vw.FuzzResultClusters()
home_url = "http://www.webscantest.com/"
top_url = "http://testphp.vulnweb.com/"

urls = []
parameters = []
urls.append("/datastore/search_by_id.php") #only int
urls.append("/datastore/search_by_name.php") #only strings + two quotes
urls.append("/datastore/search_double_by_name.php") # only slashes and two quotes
urls.append("/datastore/search_by_statement.php") # search for rake
urls.append("/datastore/search_get_by_name.php")
urls.append("/datastore/search_single_by_name.php") #only slashes and single quote
parameters.append("id")
parameters.append("name")

# for url in urls:
#     print("URL Fuzzing: ", url)
#     if "id" in url :
#         vw.fuzz_sql_injection(home_url+url,'id', fuzz_result_cluters)
#         vw.fuzz_xss_injection(home_url+url, 'id', fuzz_result_cluters)
#     else:
#         vw.fuzz_sql_injection(home_url + url, 'name', fuzz_result_cluters)
#         vw.fuzz_xss_injection(home_url + url, 'name', fuzz_result_cluters)



vw.fuzz_sql_injection(top_url+"search.php",'test', fuzz_result_cluters)
vw.fuzz_xss_injection(top_url+"search.php",'test', fuzz_result_cluters)