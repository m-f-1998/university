import json
import pycountry_convert as pc


class ViewsByLocation:
    def countries(self) -> dict:
        """
        Returns Countries Where self.subject_doc_id Has Been Read
        :return: loc_count
        :rtype: dict
        """
        # loc_count => Key: Alpha_Country_Code, Value: Count of Views On Class Doc From Key
        loc_count, key = {}, "visitor_country"
        try:
            with open(self.data_file, "r") as f:
                for doc in [json.loads(line) for line in f]:
                    try:
                        if doc["env_type"] == "reader" and doc["subject_doc_id"] == self.subject_doc_id:
                            loc_count[doc[key]] = loc_count.setdefault(doc[key], 0) + 1
                    except KeyError:
                        pass

        except EnvironmentError:
            raise Exception("File Not Found")
        return loc_count

    def continents(self) -> dict:
        """
        Returns Continents Where self.subject_doc_id Has Been Read
        :return: loc_count
        :rtype: dict
        """
        # loc_count => Key: Alpha_Continent_Code, Value: Count of Views On Class Doc From Key
        loc_count = {}
        for country_code in self.countries().keys():
            # Library Call To Get A Country_Alpha_Code's Continent
            continent = pc.country_alpha2_to_continent_code(country_code)
            loc_count[continent] = loc_count.setdefault(continent, 0) + 1
        return loc_count

    def __init__(self, subject_doc_id, data_file):
        self.subject_doc_id = subject_doc_id  # Document UUID To Be Examined
        self.data_file = data_file  # File Containing JSON Data
