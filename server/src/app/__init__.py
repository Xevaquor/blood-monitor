from flask import Flask, render_template, session, redirect, url_for
import flask.ext.sqlalchemy
from flask.ext.session import Session

app = Flask(__name__)
app.config.from_object('config')

db = flask.ext.sqlalchemy.SQLAlchemy(app)
Session(app)

@app.errorhandler(404)
def not_found(error):
    return render_template('404.html'), 404

@app.errorhandler(403)
def not_found(error):
    return render_template('403.html'), 403


from app.mod_blood.controllers import mod_blood as blood_module
from app.mod_user.controllers import mod_user as user_module
from app.mod_auth.controllers import mod_auth as auth_module


app.register_blueprint(blood_module)
app.register_blueprint(user_module)
app.register_blueprint(auth_module)

from jinja2 import Environment, PackageLoader
env = Environment(loader=PackageLoader('app', 'templates'))
env.globals['session'] = session


@app.route('/')
def index():
    return redirect(url_for('blood.index'))
