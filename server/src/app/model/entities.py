from app import db
from dateutil import parser

class User(db.Model):
    __tablename__ = 'user'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Unicode(256), nullable=False, unique=True, index=True)
    password = db.Column(db.Unicode(64), nullable=False)
    salt = db.Column(db.Unicode(255), nullable=False)


class Measurement(db.Model):
    __tablename__ = 'measurement'

    id = db.Column(db.Integer, primary_key=True)
    pulse = db.Column(db.Integer, nullable=False)
    systolic = db.Column(db.Integer, nullable=False)
    diastolic = db.Column(db.Integer, nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, index=True)
    date = db.Column(db.DateTime, nullable=False)

    def serialize(self):
        return {
            'id': self.id,
            'pulse': self.pulse,
            'systolic': self.systolic,
            'diastolic': self.diastolic,
            'user_id': self.user_id,
            'date': self.date,
        }

    def deserialize(self, jsondict):
        self.pulse = jsondict.get('pulse')
        self.systolic = jsondict.get('systolic')
        self.diastolic = jsondict.get('diastolic')
        if jsondict.get('date') is not None:
            self.date = parser.parse(jsondict.get('date'))


class MeasurementValidator(object):
    def __init__(self, m_func):
        self.m_func = m_func

    def validate(self):
        return [], True