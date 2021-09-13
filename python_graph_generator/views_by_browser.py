import json
import re


class ViewsByBrowser:
    def get_browser(self, verbose_flag, complex_browser=False) -> dict:
        """
        Returns Most Popular Browsers Used To View Documents
        :param verbose_flag -> bool
        :param complex_browser -> bool
        :return: browser_count
        :rtype: dict
        """
        browser_count, key = {}, "visitor_useragent"
        try:
            with open(self.data_file, "r") as f:
                missed_web_browser = False
                for doc in [json.loads(line) for line in f]:
                    # If Processing Verbose Browser Strings
                    try:
                        if verbose_flag:
                            # If Narrowing Browsers Down By Type (Default = False)
                            browser = re.search(
                                "((?i)(firefox|msie|chrome|safari|iphone|ipad|nokia|trident|opera|android))|(iPhone|iPad)[/\s]([/\d.]+)",
                                doc[key]
                            ) if complex_browser else re.search("^.*?(?=\/)", doc[key])
                            if browser:
                                browser = browser.group(0).upper()
                                if browser.find(" ") != -1:
                                    browser = browser[:browser.index(" ")]
                                elif browser.find("/") != -1:
                                    browser = browser[:browser.index("/")]
                                if complex_browser:
                                    if browser == "TRIDENT":
                                        browser = "MSIE"
                                    elif browser == "IPHONE" or browser == "IPAD":
                                        browser = "IOS"
                                    elif browser == "NOKIA":
                                        browser = "WINDOWS\nMOBILE"
                                # Count Occurrences Of Browser - Default To 0 For Empty Case
                                browser_count[browser] = browser_count.setdefault(browser, 0) + 1
                            else:
                                if not missed_web_browser and complex_browser: missed_web_browser = True
                        else:
                            browser_count[doc[key]] = browser_count.setdefault(doc[key], 0) + 1
                    except KeyError:
                        pass
                if missed_web_browser: print("Some Browser User Agent Strings Were Unrecognised")
            return browser_count
        except EnvironmentError:
            raise Exception("File not found")


    def __init__(self, data_file):
        self.data_file = data_file  # File Containing JSON Data
