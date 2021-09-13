import json

from graphviz import Digraph


class AlsoLikes:
    def _file_len(self) -> str:
        """
        Returns Number of Lines In args.f File
        :return: file_lines
        :rtype: str
        """
        try:
            with open(self.data_file) as f:
                for file_lines, l in enumerate(f):
                    pass
            if file_lines + 1 > 1000000:
                return str(file_lines + 1)[0] + 'm'
            elif file_lines + 1 > 100000:
                return str(file_lines + 1)[0:3] + 'k'
            elif file_lines + 1 > 10000:
                return str(file_lines + 1)[0:2] + 'k'
            elif file_lines + 1 > 1000:
                return str(file_lines + 1)[0] + 'k'
            return str(file_lines + 1)
        except EnvironmentError:
            raise Exception("File not found")

    def users_docs_search(self, uuid, key, return_key) -> set:
        """
        Get All Users Who Have Read A Document or Documents Read By A Users
        :param uuid -> string
        :param key -> string
        :param return_key -> string
        :return: uuid_list
        :rtype: set
        """
        uuid_list = set()
        try:
            with open(self.data_file, "r") as f:
                for doc in [json.loads(line) for line in f]:
                    try:
                        if doc["env_type"] == "reader" and doc["event_type"] == "read" and doc[key] == uuid:
                            uuid_list.add(doc[return_key])
                    except KeyError:
                        pass
            return uuid_list
        except EnvironmentError:
            raise Exception("File not found")

    def __also_likes_graph(self, graph_documents, graph_documents_nodes, do_not_open):
        """
        Generate Graph From Params ( Dictionary of Keys=All Users and Values=Documents List For Each Users, Set Of Documents - Single Set Of All Documentse Without Duplicates, Bool Flag For Show )
        :param graph_documents -> dict
        :param graph_documents_nodes -> set
        :param do_not_open -> bool
        """
        graph = Digraph('also_likes',
                      filename='also_likes_graph.gv',
                      node_attr={'shape': 'plaintext', 'fontsize': '16'},
                      graph_attr={'ranksep': '.75', 'ratio': 'compress', 'size': '5,22', 'orientation': 'landscape',
                                  'rotate': '180'})
        graph.node("Users")
        graph.node("Documents")
        graph.edge("Users", "Documents", label="   Size:" + self._file_len() + " lines")

        # Add User Nodes
        for user, doc in graph_documents.items():
            graph.attr('node', shape="box", rank="users")
            if user != self.visitor_uuid:
                graph.node(user[-4:])

        # Add Document Nodes
        for doc in graph_documents_nodes:
            graph.attr('node', shape="circle", rank="document")
            if doc != self.document_uuid:
                graph.node(doc[-4:])

        # Add Origin Nodes
        graph.attr('node', shape="circle", rank="documents", color="green", style="filled")
        graph.node(self.document_uuid[-4:])
        if self.visitor_uuid is not None:
            graph.attr('node', shape="box", rank="users", color="green", style="filled")
            graph.node(self.visitor_uuid[-4:])
            graph.edge(self.visitor_uuid[-4:], self.document_uuid[-4:])

        # Add Edges
        for user, docs in graph_documents.items():
            for doc in docs:
                graph.edge(user[-4:], doc[-4:])

        if do_not_open:
            graph.view()
        else:
            graph.render('also_likes_graph.gv')

    def liked_documents(self, process_graph, show_graph):
        """
        Get Dictionary Of Documents Similar To That Read By User (Result Includes Original Doc and User)
        :param process_graph -> bool
        :param show_graph -> bool
        :return: result (optional)
        """
        liked_documents, graph_documents, graph_documents_nodes = {}, {}, set()
        users_read_current_doc = self.users_docs_search(self.document_uuid, "subject_doc_id", "visitor_uuid")

        for user in users_read_current_doc:
            docs_read_by_users = self.users_docs_search(user, "visitor_uuid", "subject_doc_id")
            user_docs = set()
            for doc in docs_read_by_users:
                liked_documents[doc] = liked_documents.setdefault(doc, 0) + 1
                if user != self.visitor_uuid or user_docs == self.document_uuid:
                    user_docs.add(doc)
            graph_documents[user] = user_docs

        ordered = sorted(liked_documents.items(), key=lambda x: (x[1], x[0]), reverse=True)[:11]

        for user in graph_documents.copy().keys():
            for doc in graph_documents[user].copy():
                flag = True
                for ordered_doc in ordered:
                    if doc == ordered_doc[0]:
                        flag = False
                        break
                if flag and self.visitor_uuid != doc: graph_documents[user].remove(doc)

        for user, docs in graph_documents.items():
            for file in docs:
                graph_documents_nodes.add(file)

        if process_graph:
            self.__also_likes_graph(graph_documents, graph_documents_nodes, show_graph)
        else:
            result = "Documents Like " + self.document_uuid[-4:] + " Are:"
            if len(ordered) == 0:
                result += "\nNone Found"
            else:
                for file in ordered:
                    if file[0] != self.document_uuid:
                        result += "\n" + str(file[0][-4:])
            return result

    def __init__(self, data_file, document_uuid, visitor_uuid):
        self.data_file = data_file  # File Containing JSON Data
        self.document_uuid = document_uuid  # Document UUID To Be Examined
        self.visitor_uuid = visitor_uuid  # Visitor UUID To Be Examined
