from abc import ABC, abstractmethod
from dataclasses import dataclass

from ..utils.database import Database

@dataclass
class ModelBase(ABC):
    def set_db_instance(self, db:Database):
        self.database = db
        
    def load_from_db(self, uuid:str):
        self.database.load(self.get_entity_name(), uuid)
        
    def save_self(self):
        print('save_self, param: entity_name={entity_name}, entity_id={entity_id}'.format(entity_name=self.get_entity_name(), entity_id=self.__get_entity_id()))
        self.database.save(self.get_entity_name(), self.__get_entity_id(), self)
        
    @abstractmethod
    def get_entity_name(self):
        pass
        
    @abstractmethod
    def __get_entity_id(self):
        pass