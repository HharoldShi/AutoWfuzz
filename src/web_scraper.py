from urllib.request import urlopen
import urllib
from bs4 import BeautifulSoup
import re


class URL:
    def __init__(self, url):
        self.url = url
        self.getparams = []
        self.postparams = []


# root_url =

class ScrapedURLs:
    def __init__(self, root_url):
        self.root = root_url
        self.urls = {self.root}
        self.keywords = set()
        self.form_keywords = set()
        self.visited = set()
        self.forms = dict()
        self.get_urls = dict()
        self.get_keywords = set()

    def reset(self):
        self.urls = {self.root}
        self.keywords = set()
        self.form_keywords = set()
        self.visited = set()
        self.forms = dict()

    def scrape_all(self):
        if len(self.urls) <= len(self.visited):
            return self.output()
        else:
            temp_urls = set()
            temp_keywords = set()
            temp_visited = set()

            unvisited = self.urls - self.visited

            # update keywords
            for u in unvisited:
                fragments = u[7:].split("/")
                fragments = set(fragments) - {""}
                if len(fragments | self.keywords) > len(self.keywords):
                    temp_keywords.update(fragments)

            existing_keywords = temp_keywords | self.keywords

            # scrape
            for u in unvisited:
                fragments = u[7:].split("/")
                fragments = set(fragments) - {""}

                if len(fragments | self.keywords) > len(self.keywords):
                    print("Scraping from " + u)
                    url, get_url, form = self.scrape(u)
                    if url:
                        for i in url:
                            # if i.find('?') == -1:
                            _frag = i[7:].split("/")
                            _frag = set(_frag) - {""}

                            if len(_frag | existing_keywords) > len(existing_keywords):
                                existing_keywords.update(_frag)
                                r = i
                                if i[-1] == '/':
                                    r = i[:-1]
                                temp_urls.add(i)

                    if get_url:
                        for i in get_url:
                            r = i
                            if i[-1] == "/":
                                r = i[:-1]
                            _frag = r[7:].split("/")
                            m = _frag[-1]
                            key = r[:(m.find('=') - len(m))]
                            if key not in self.get_keywords:
                                self.get_keywords.add(key)

                                self.get_urls.update({r: []})

                    if form:
                        for key in form:
                            r = key[0].rstrip('/')
                            _frag = r[7:].split("/")

                            if _frag[-1] not in self.form_keywords:
                                self.form_keywords.add(_frag[-1])
                                self.forms.update({r: form[key]})

                    temp_visited.add(u)

            self.visited.update(temp_visited)
            self.urls.update(temp_urls)
            self.keywords.update(temp_keywords)

            return self.scrape_all()

    def scrape(self, url):
        try:
            page = urlopen(url)
        except urllib.error.HTTPError as e:
            # print('HTTPError: {}'.format(e.code))
            return [], [], {}
        html = page.read().decode("utf-8")
        soup = BeautifulSoup(html, "html.parser")
        urls = self.find_url(soup, url)
        good, get = self.filter_url(urls)
        forms = self.find_form(soup, url)
        return good, get, forms

    def find_url(self, soup, url):
        if url[-1] != '/':
            url = url + '/'

        links = soup.find_all("a")
        if links == []:
            return []

        sub_urls = []
        for link in links:
            i = ''
            try:
                i = link["href"]
            except:
                print("no href on " + url)
                continue

            i = link["href"]
            i = re.sub(r"\/*", "", i)
            if re.match("http", i):
                pass
            elif re.match("mailto:", i):
                pass
            else:
                sub_urls.append(url + i)
        return sub_urls

    def filter_url(self, url_list):
        good_urls = []
        get_urls = []
        for i in url_list:
            t = i.rstrip('/')
            _frag = t.split('/')[-1]
            if '?' in _frag and '=' in _frag:
                get_urls.append(t)
            else:
                good_urls.append(t)
        return good_urls, get_urls

    def find_form(self, soup, url):
        if url[-1] != '/':
            url = url + '/'

        forms = soup.find_all('form')
        actions = {}

        for form in forms:

            try:
                actions[(url, form['action'])] = []
                flag = True
            except:
                print("no action on " + url)
                actions[(url, "")] = []
                flag = False
                # continue

            for element in form.find_all('input'):
                if re.search('name', str(element)):
                    if flag:
                        actions[(url, form['action'])].append(element['name'])
                    else:
                        actions[(url, '')].append(element['name'])
                    # print(element['name'])
        return actions

    def output(self):
        output_list = []
        allurls = {**self.forms, **self.get_urls}
        for i in allurls:
            _frag = i.split('/')[-1]
            # get
            if '?' in _frag and '=' in _frag:
                # _frag.split('=')[-1]
                # i[:(i.rfind("="))]

                u = URL(i[:(i.rfind("?"))])
                s = i.rfind("?") + 1
                e = - (len(i[s:]) - i[s:].find("="))
                tmp = i[s:e]
                u.getparams.append(tmp)
                output_list.append(u)

            # post
            else:
                u = URL(i)
                u.postparams = self.forms[i]
                output_list.append(u)
        return output_list
