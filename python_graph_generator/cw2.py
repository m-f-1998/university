#!/usr/bin/env python3
# -*- coding: iso-8859-15 -*-

import argparse
import sys

import also_likes as likes
import cw2_gui
import views_by_browser as brow
import views_by_location as loc

'''
Exemplar Data:
    File: ./issuu_cw2.json
    Document UUI: 140228015854-304fea5abcd6a0c3e0be01138bffa713
    User UUI: 5fb18c7e8cda08a5
'''


def main():
    """
    CMD-Line Interface
    """
    parser = argparse.ArgumentParser()  # Parses CMD Line Arguments With Help Text

    parser.add_argument('-u', type=str, help='User UUID')  # OPTIONAL
    parser.add_argument('-d', type=str, help='Document UUID')
    parser.add_argument('-t', type=str, help='Task ID from Specification')
    parser.add_argument('-f', type=str, help='JSON File With Input Data')
    parser.add_argument('-b', type=bool, help='High Level Browser Sorting', default=False)  # OPTIONAL
    args = parser.parse_args()

    if args.d is not None and args.t is not None and args.f is not None:

        # Returns Histogram Showing Countries Where args.d Has Been Viewed
        if args.t == "2a":
            cw2_gui.show_histogram(loc.ViewsByLocation(args.d, args.f).countries(), "Countries",
                                   "Countries Where Document " + args.d[-4:] + " Was Read \n")

        # Returns Histogram Showing Continents Where args.d Has Been Viewed
        elif args.t == "2b":
            cw2_gui.show_histogram(loc.ViewsByLocation(args.d, args.f).continents(), "Continents",
                                   "Continents Where Document " + args.d[-4:] + " Was Read \n")

        # Returns Histogram Browsers Used To View Documents (Full Web Browser String With No Sorting)
        elif args.t == "3a":
            print("Browser Strings Can Be Long Using This Method And Cause Rendering Errors.\n-t 3b Is Preferred.")
            cw2_gui.show_histogram(brow.ViewsByBrowser(args.f).get_browser(False), "Browsers",
                                   "Most Popular Browsers In File " + args.f + "\n")

        # Returns Histogram Browsers Used To View Documents (With Sorting,
        #       args.b Denotes High Level Web Browser Soring {False} Or Sorting By Individual Browser {True}
        # )
        elif args.t == "3b":
            cw2_gui.show_histogram(brow.ViewsByBrowser(args.f).get_browser(True, args.b), "Browsers",
                                   "Most Popular Browsers In File " + args.f + "\n")

        # Returns Documents That Are Similar To args.d Which Is Read By args.u
        elif args.t == "4d":
            s = likes.AlsoLikes(args.f, args.d, args.u).liked_documents(False, False)
            print(s)

        # Returns Graph Of Documents Similar To args.d Which Is Read By args.u
        #   Saves File As .gv and .pdf
        elif args.t == "5":
            likes.AlsoLikes(args.f, args.d, args.u).liked_documents(True, False)
            print("Graph Saved To Current Directory")

        # Task 6 Opens The Graph Automatically In A PDF Viewer and Then Prompts User Decision On Whether To Open GUI
        elif args.t == "6":
            likes.AlsoLikes(args.f, args.d, args.u).liked_documents(True, True)
            if input("Do You Wish To Open The CW2 GUI (Y/N)? ").upper() == 'Y':
                cw2_gui.GUI(args.f, args.d, args.u).make_form()

    else:
        # Opens the GUI if any of the arguments are undefined
        cw2_gui.GUI(args.f, args.d, args.u).make_form()


if __name__ == "__main__":  # execute only if run as a script
    main()
