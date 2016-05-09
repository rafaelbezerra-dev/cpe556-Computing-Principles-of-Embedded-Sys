from flask import Flask, request, Response, json, jsonify
from flask_restful import reqparse, abort, Api, Resource
from contracts import *
from client_api import *


def abort_if_message_doesnt_exist(message_id):
    if message_id not in MESSAGES:
        abort(404, message="Message {} doesn't exist".format(message_id))


class MessageAPI(Resource):
    @staticmethod
    def register_routes(api):
        api.add_resource(MessageAPI, '/message/<message_id>')

    def get(self, message_id):
        abort_if_message_doesnt_exist(message_id)
        return MESSAGES[message_id].serialize()


class MessageListAPI(Resource):
    @staticmethod
    def register_routes(api):
        api.add_resource(MessageListAPI, '/messages')

    def get(self):
        l = [o.serialize() for o in MESSAGES.values()]
        return l


    def post(self):
        message_content = request.json['messageContent']
        client_id = int(request.json['clientId'])

        abort_if_client_doesnt_exist(client_id)

        message_id = len(MESSAGES) + 1
        MESSAGES[message_id] = Message(message_id, message_content, client_id)
        return MESSAGES[message_id].serialize(), 201