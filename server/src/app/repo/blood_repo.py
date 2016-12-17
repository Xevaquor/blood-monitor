from flask import flash
from flask import jsonify

import app

from app import db
from app.model.entities import Measurement, User, MeasurementValidator
from dateutil import parser
import datetime


def get_by_id(measurement_id):
    return Measurement.query.get(measurement_id)


def get_by_date(user_id, from_date=None, to_date=None):
    measurements = Measurement.query.filter_by(user_id=user_id)
    if from_date:
        measurements = measurements.filter(Measurement.date >= from_date)
    if to_date:
        to_date = datetime.datetime.combine(to_date, datetime.time(23, 59))
        measurements = measurements.filter(Measurement.date <= to_date)

    return list(measurements.order_by(Measurement.date))


def get_all(user_id):
    query = Measurement.query.filter(Measurement.user_id == user_id).order_by(Measurement.date.desc())

    measurements = query.all()
    return list(measurements)


def add(measurement):
    validator = MeasurementValidator(lambda: measurement)

    errors, valid = validator.validate()
    if not valid:
        return errors

    db.session.add(measurement)
    db.session.commit()


def update(measurement):
    validator = MeasurementValidator(lambda: measurement)

    errors, valid = validator.validate()
    if not valid:
        return errors

    db.session.commit()


def delete(measurement):
    db.session.delete(measurement)
    db.session.commit()
