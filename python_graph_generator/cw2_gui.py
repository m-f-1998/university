import tkinter as tk
from tkinter import messagebox

import matplotlib.pyplot as plt

import also_likes as likes
import views_by_browser as brow
import views_by_location as loc


def show_histogram(data, x_label, title):
    """
    Show A Histogram From A Data Dictionary Passed As Param With Titles For Data and Graph Title
    Histogram Needs To Be Available To Other Functions Such As Location & Browser Count
    Thus Not Defined In GUI Class
    :param data -> dict
    :param x_label -> str
    :param title -> str
    """
    fig, ax = plt.subplots(figsize=(12 if len(data) < 10 else len(data) / 6, 6))
    ax.bar(list(data.keys()), data.values(), color='g')
    plt.xlabel(x_label)
    plt.ylabel('Count')
    plt.xticks(fontsize=8, rotation=45)
    plt.title(title)
    plt.show()

class GUI:
    entries = {}  # Collects Data In GUI

    def show_error(self, message):
        """
        Show A PopUp Error Notification
        :param message -> str
        """
        self.root.withdraw()
        messagebox.showerror("Error", message)
        self.root.deiconify()

    def views_by_country(self):
        """
        Process Request For Task 2a (See cw2.py for details)
        """
        if self.entries["Data File"].get() != '' and self.entries['Document UUID'].get() != '':
            show_histogram(
                loc.ViewsByLocation(
                    (self.entries['Document UUID'].get()).strip('\n'),
                    (self.entries["Data File"].get()).strip('\n')
                ).countries(),
                "Countries",
                "Countries Where Document " + self.entries['Document UUID'].get().strip('\n')[-4:] + " Was Read \n")
        else:
            self.show_error("Data File and Document UUID Required")

    def views_by_continent(self):
        """
        Process Request For Task 2b (See cw2.py for details)
        """
        if self.entries["Data File"].get() != '' and self.entries['Document UUID'].get() != '':
            show_histogram(
                loc.ViewsByLocation(
                    (self.entries['Document UUID'].get()).strip('\n'),
                    (self.entries["Data File"].get()).strip('\n')
                ).continents(),
                "Continents",
                "Continents Where Document " + self.entries['Document UUID'].get().strip('\n')[-4:] + " Was Read \n")
        else:
            self.show_error("Data File and Document UUID Required")

    def views_by_browser(self):
        """
        Process Request For Task 3b (See cw2.py for details)
        """
        if self.entries["Data File"].get() != '':
            show_histogram(
                brow.ViewsByBrowser((self.entries["Data File"].get()).strip('\n')).get_browser(True),
                "Browsers",
                "Most Popular Browsers In File " + (self.entries["Data File"].get()).strip('\n') + "\n")
        else:
            self.show_error("Data File Required")

    def also_likes(self):
        """
        Process Request For Task 4d (See cw2.py for details)
        """
        if self.entries["Data File"].get() != '' and self.entries['Document UUID'].get() != '':
            s = likes.AlsoLikes(
                (self.entries["Data File"].get()).strip('\n'),
                (self.entries['Document UUID'].get()).strip('\n'),
                (self.entries['User UUID'].get()).strip('\n')
            ).liked_documents(False, False)
            window = tk.Toplevel()
            label = tk.Label(window, text=s)
            label.pack()
        else:
            self.show_error("Data File and Document UUID Required")

    # Process Request For Task 5 (See cw2.py for details)
    def also_likes_graph(self):
        """
        Process Request For Task 5 (See cw2.py for details)
        """
        if self.entries["Data File"].get() != '' and self.entries['Document UUID'].get() != '':
            user = (self.entries['User UUID'].get()).strip('\n')
            if (self.entries['User UUID'].get()).strip('\n') == "":
                user = None
            likes.AlsoLikes(
                (self.entries["Data File"].get()).strip('\n'),
                (self.entries['Document UUID'].get()).strip('\n'),
                user
            ).liked_documents(True, True)
        else:
            self.show_error("Data File and Document UUID Required")

    def make_form(self):
        """
        # Make The GUI Form
        """
        self.root.title("Industrial Programming Coursework 2")

        btn_titles = [
            ["Get Views By Country", "Get Views By Continent"],
            ["Get Views By Browser"],
            ["Get 'Also Likes' List", "Get 'Also Likes' Graph"]
        ]
        btn_fn = [
            [self.views_by_country, self.views_by_continent],
            [self.views_by_browser],
            [self.also_likes, self.also_likes_graph]
        ]
        labels = ["Data File", "Document UUID", "User UUID"]

        for t in range(0, len(self.data)):  # Grid Format - Add Labels With Data Entries
            row = tk.Frame(self.root)
            lab = tk.Label(row, width=12, text=labels[t] + ": ", anchor='w')
            ent = tk.Entry(row)
            if self.data[t] is not None:
                ent.insert(0, self.data[t])  # Auto Fill Data If Included On Command Line
            row.pack(side=tk.TOP, fill=tk.X, padx=5, pady=5)
            lab.pack(side=tk.LEFT)
            ent.pack(side=tk.RIGHT, expand=tk.YES, fill=tk.X)
            self.entries[labels[t]] = ent

        if len(btn_titles) == len(btn_fn):
            for i in range(0, len(btn_titles)):  # Add Buttons To GUI
                row, btn = tk.Frame(self.root), []
                for j in range(0, len(btn_titles[i])):
                    btn.append(tk.Button(row, width=22, text=btn_titles[i][j], anchor='w', command=(btn_fn[i][j])))
                row.pack(side=tk.TOP, fill=tk.X, padx=5, pady=5)
                btn[0].pack(side=tk.LEFT, expand=tk.YES, fill=tk.X)
                if len(btn) == 2: btn[1].pack(side=tk.RIGHT, expand=tk.YES, fill=tk.X)

        self.root.mainloop()

    def __init__(self, data_file, document_uuid, visitor_uuid):
        self.root = tk.Tk()  # Main GUI Window
        self.data = [data_file, document_uuid,
                     visitor_uuid]  # Input JSON Data, Document UUID & User UUID To Be Examined
