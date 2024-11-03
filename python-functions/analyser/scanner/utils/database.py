from abc import ABC, abstractmethod

class Database(ABC):
    @abstractmethod
    def save(self, table:str, id:str, obj):
        pass
    @abstractmethod
    def load(self, table:str, id:str):
        pass
    @abstractmethod
    def load_all(self, table:str):
        pass

class FakeDatabase(Database):
    def __init__(self):
        self.data_dict={}
    
    def save(self, table:str, id:str, obj):
        if table not in self.data_dict:
            self.data_dict[table] = {}
            
        self.data_dict[table][id] = obj
    
    def load(self, table:str, id:str):
        if table in self.data_dict and id in self.data_dict[table]:
            return self.data_dict[table][id]
        else:
            return None
    
    def load_all(self, table:str):
        if table in self.data_dict:
            return list(self.data_dict[table].values())
        else:
            return None