from flask import Blueprint, request, render_template, flash, g, session, redirect, url_for, abort

from app import db

from app.model.entities import User

from app.pass_utils import PasswordUtil

from app.model.validator import *

from app.mod_auth.autorization_required import requires_sign_in, requires_not_signed_in, \
    requires_admin
from app.repo import user_repo
from app.repo.user_repo import valid_password

mod_user = Blueprint('user', __name__, url_prefix='/user')


@mod_user.route('/signup', methods=['GET'])
@requires_not_signed_in()
def sign_up():
    return render_template('user/sign_up.html')


@mod_user.route('/signup', methods=['POST'])
@requires_not_signed_in()
def create():
    username = request.form['username']
    password = request.form['password']
    password_confirmation = request.form['password_confirmation']

    validator = CombinedValidator(validators=[
        PasswordValidator(lambda: password, lambda: password_confirmation),
        UniqueUsernameValidator(lambda: username)
    ])

    errors, valid = validator.validate()
    if not valid:
        for e in errors:
            flash(e.message)
        return redirect(url_for('user.sign_up'))

    pu = PasswordUtil()
    user = User()
    user.name = username
    user.salt = pu.generate_salt()
    user.password = pu.hash_password(password, user.salt)

    db.session.add(user)
    db.session.commit()

    flash('Możesz się teraz zalogować')
    return redirect(url_for('user.sign_in'))


@mod_user.route('/signin', methods=['GET'])
@requires_not_signed_in()
def sign_in():
    return render_template('user/sign_in.html')


@mod_user.route('/signin', methods=['POST'])
@requires_not_signed_in()
def sign_in_post():
    username = request.form['username']
    password = request.form['password']
    signed_in = valid_password(username, password)
    if signed_in:
        flash('Zalogowano jako ' + username)
        session['username'] = username
        session['user_id'] = User.query.filter_by(name=username).first().id
        return redirect(url_for('index'))
    else:
        flash('Niepoprawy email i/lub hasło', 'alert-warning')
        return redirect(url_for('user.sign_in'), code=303)


@mod_user.route('/signout', methods=['POST'])
def sign_out():
    if 'username' in session:
        del session['username']
        del session['user_id']
        if 'recent' in session: del session['recent']
    flash('Wylogowano')
    return redirect(url_for('index'))

