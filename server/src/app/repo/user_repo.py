import app
from app.exceptions import BloodException

from app.model.entities import User

from app.pass_utils import PasswordUtil

pu = PasswordUtil()


def user_exists(username):
    users = User.query.filter_by(name=username).all()
    if len(users) > 0:
        return True
    return False


def add_user(username, plain_password):
    if user_exists(username):
        raise BloodException("Użytkownik o takiej nazwie już istnieje")

    user = User()
    user.name = username
    user.salt = pu.generate_salt()
    user.password = pu.hash_password(plain_password, user.salt)

    app.db.session.add(user)
    app.db.session.commit()


def valid_password(username, plain_password):
    users = User.query.filter_by(name=username).all()
    if not users:
        return False
    u = users[0]
    pass_hash = pu.hash_password(plain_password, u.salt)
    return pass_hash == u.password


def is_rest_call(request):
    return request.headers.environ.get('HTTP_ACCEPT') == 'application/json'

def get_rest_user_id(request):
    if valid_password(request.authorization.username, request.authorization.password):
        return User.query.filter_by(name=request.authorization.username).first().id

def get_rest_user_id_by_name(name):
    return User.query.filter_by(name=name).first().id
