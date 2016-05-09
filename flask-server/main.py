from flask import Flask, request, Response, json
from flask_restful import reqparse, abort, Api, Resource

app = Flask(__name__)
api = Api(app)

CLIENTS = {
    1 : {'id': 1, 'name': 'rafa'},
    2 : {'id': 2, 'name': 'yasmin'},
    3 : {'id': 3, 'name': 'john'},
    4 : {'id': 4, 'name': 'sara'},
}

def abort_if_client_doesnt_exist(client_id):
    if client_id not in TODOS:
        abort(404, message="Client {} doesn't exist".format(client_id))

parser = reqparse.RequestParser()
parser.add_argument('task')


class Client(Resource):
    def get(self, client_id):
        abort_if_client_doesnt_exist(client_id)
        return CLIENTS[client_id]


class ClientList(Resource):
    def get(self):
        return CLIENTS.values()

    def post(self):
        request_data = None
        if request.headers['Content-Type'] == 'application/json':
            request_data = json.dumps(request.json)
        client_id = len(CLIENTS) + 1
        CLIENTS[client_id] = {'id': client_id, 'name': request.json['name']}
        return CLIENTS[client_id], 201
##
## Actually setup the Api resource routing here
##
api.add_resource(ClientList, '/clients')
api.add_resource(Client, '/client/<client_id>')


if __name__ == '__main__':
    app.run(debug=True)
