#!/usr/bin/env python
# -*- coding:utf-8 -*-
import vulnweb_wfuzz as vw
import web_scraper as ws
import sys
import getopt
import random


def random_input_generator(min_len, max_len, char_code_start, char_code_end):
    string_len = random.randrange(min_len, max_len + 1)
    out_str = ""
    for i in range(0, string_len):
        out_str += chr(random.randrange(char_code_start, char_code_end))
    return out_str


def random_word_generator(min_len, max_len):
    string_len = random.randrange(min_len, max_len + 1)
    out_str = ""
    for i in range(0, string_len):
        if random.randrange(0, 2) == 0:
            out_str += chr(random.randrange(65, 90))
        else:
            out_str += chr(random.randrange(97, 122))
    return out_str


def gen_random_input_file(filename):
    fh = open(filename, "w")
    for i in range(40):
        fh.write(random_input_generator(0, 20, 48, 57) + "\n") # random_number
    for i in range(40):
        fh.write(random_word_generator(0, 100) + "\n") # random_word
    for i in range(40):
        fh.write(random_input_generator(0, 200, 32, 126) + "\n") # random string
    fh.close()


def random_fuzzing(url):
    payload = "-z file,random_input.txt"
    if len(url.getparams) != 0:
        sys.stdout.write("\nRandom Fuzzing on URL parameters, URL = " + url.url + "\n")
        vw.wfuzz_get_request(payload, url.url, url.getparams)

    if len(url.postparams) != 0:
        sys.stdout.write("\nRandom Fuzzing on POST parameters, URL = " + url.url + "\n")
        vw.wfuzz_post_request(payload, url.url, url.postparams)


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

    gen_random_input_file("random_input.txt")

    # perform fuzzing
    print("\nPerform Random Fuzzing on the Found URLs....")
    for url in url_list:
        random_fuzzing(url)


def test_main():
    gen_random_input_file("random_input.txt")
    url = ws.URL("http://testphp.vulnweb.com/artists.php/artists.php")
    url.getparams = ["artist"]
    random_fuzzing(url)


# "http://www.webscantest.com/"
# "http://testphp.vulnweb.com/"
# "http://www.testfire.net/"

if __name__ == '__main__':
    main()
    # test_main()