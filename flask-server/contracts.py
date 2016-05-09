class Client:
    def __init__(self, client_id=0, client_name=None):
        self.client_id = client_id
        self.client_name = client_name

    def serialize(self):
        return {
            'clientId': self.client_id,
            'clientName': self.client_name,
        }


class Message:
    def __init__(self, message_id=0, content=None, client_id=0):
        self.message_id = message_id
        self.content = content
        self.client_id = client_id

    def serialize(self):
        return {
            'messageId': self.message_id,
            'messageContent': self.content,
            'clientId': self.client_id,
        }


# CLIENTS = {
#     0: {'id': 1, 'name': 'rafa'},
#     1: {'id': 1, 'name': 'lucy'},
#     2: {'id': 2, 'name': 'yasmin'},
#     3: {'id': 3, 'name': 'john'},
#     4: {'id': 4, 'name': 'sara'},
# }

CLIENTS = {
    0: Client(0, 'rafa'),
    1: Client(1, 'yasmin'),
    2: Client(2, 'john'),
    3: Client(3, 'sara'),
}

MESSAGES = {
    0: Message(0, 'Hi', 0),
    1: Message(1, 'Hi there.', 0),
}
