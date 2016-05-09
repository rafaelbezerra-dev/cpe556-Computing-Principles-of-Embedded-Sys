from flask import Flask
from flask_restful import Api
from client_api import *

# CLIENTS = {
#     1: {'id': 1, 'name': 'rafa'},
#     2: {'id': 2, 'name': 'yasmin'},
#     3: {'id': 3, 'name': 'john'},
#     4: {'id': 4, 'name': 'sara'},
# }
#
#
# def abort_if_client_doesnt_exist(client_id):
#     if client_id not in CLIENTS:
#         abort(404, message="Client {} doesn't exist".format(client_id))
#
#
# class Client(Resource):
#     def get(self, client_id):
#         abort_if_client_doesnt_exist(client_id)
#         return CLIENTS[client_id]
#
#
# class ClientList(Resource):
#     def get(self):
#         return CLIENTS.values()
#
#     def post(self):
#         request_data = None
#         if request.headers['Content-Type'] == 'application/json':
#             request_data = json.dumps(request.json)
#         client_id = len(CLIENTS) + 1
#         CLIENTS[client_id] = {'id': client_id, 'name': request.json['name']}
#         return CLIENTS[client_id], 201

def main():
    app = Flask(__name__)
    api = Api(app)
    ClientAPI.register_routes(api)
    ClientListAPI.register_routes(api)
    app.run(debug=True)


if __name__ == '__main__':
    main()
