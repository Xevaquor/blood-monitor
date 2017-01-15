import datetime
from io import BytesIO

from dateutil import parser
from flask import Blueprint, request, render_template, flash, redirect, url_for, session, jsonify
from flask import abort
from flask import send_file

from app.mod_auth.autorization_required import requires_sign_in
from app.model.entities import Measurement
from app.repo import blood_repo
from app.repo.user_repo import is_rest_call, get_rest_user_id, get_rest_user_id_by_name

mod_blood = Blueprint('blood', __name__, url_prefix='/blood')

import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt

@mod_blood.route('/', methods=['GET'])
@requires_sign_in()
def index():
    if is_rest_call(request):
        return jsonify({'rows': [x.serialize() for x in blood_repo.get_all(get_rest_user_id(request))]})
    return render_template('blood/index.html', measurements=blood_repo.get_all(session['user_id']))


@mod_blood.route('/create', methods=['GET'])
@requires_sign_in()
def create_form():
    return render_template('blood/create.html', nowdate=datetime.datetime.now().strftime('%Y-%m-%d'),
                           nowtime=datetime.datetime.now().strftime('%H:%M'),)


@mod_blood.route('/report', methods=['GET'])
@requires_sign_in()
def report_form():
    return render_template('blood/ask_report.html',
                           nowdate=datetime.datetime.now().strftime('%Y-%m-%d'),
                           olddate=(datetime.datetime.now() - datetime.timedelta(days=7)).strftime('%Y-%m-%d'))


@mod_blood.route('/report', methods=['POST'])
@requires_sign_in()
def report_post():
    from_date = request.form.get('from', None)
    to_date = request.form.get('to', None)

    if from_date:
        from_date = parser.parse(from_date)
    if to_date:
        to_date = parser.parse(to_date)

    rows = blood_repo.get_by_date(session['user_id'], from_date=from_date, to_date=to_date)

    return render_template('blood/report.html', rows=rows, from_date=from_date, to_date=to_date)


@mod_blood.route('/report/plot/', methods=['GET'])
def plot(from_date='1900-01-01', to_date='2999-11-11',username=''):
    username = request.args.get('username','')
    uid = session.get('user_id', -1)
    if uid == -1:
        uid = get_rest_user_id_by_name(username)
    from_date = request.args.get('from_date','2016-01-01')
    to_date = request.args.get('to_date', '2018-01-01')
    if from_date == '':
        from_date = datetime.datetime.now()
    else:
        from_date = parser.parse(from_date)

    if to_date == '':
        to_date = datetime.datetime.now() - datetime.timedelta(days=7)
    else:
        to_date = parser.parse(to_date)
    # if from_date is None and to_date is None

    rows = blood_repo.get_by_date(uid, from_date=from_date, to_date=to_date)
    fig, ax = plt.subplots(1)
    x = [x.date for x in rows]
    y = [x.pulse for x in rows]
    plt.plot(x, y)
    plt.plot(x, [x.systolic for x in rows])
    plt.plot(x, [x.diastolic for x in rows])
    d1 = rows[0].date
    d2 = rows[-1].date

    delta = d2 - d1
    datas=[]
    for i in range(delta.days + 1):
        datas.append(d1 + datetime.timedelta(days=i))

    plt.xticks(datas, [x.strftime('%d-%m') for x in datas], rotation='vertical')
    plt.legend(['Tetno', 'Skurczowe', 'Rozkurczowe'],loc=2)
    img = BytesIO()
    fig.savefig(img)
    img.seek(0)
    response = send_file(img, mimetype='image/png')
    response.headers['Cache-Control'] = 'no-cache, no-store, must-revalidate'

    return response


@mod_blood.route('/create', methods=['POST'])
@requires_sign_in()
def create_post():
    if is_rest_call(request):
        user_id = get_rest_user_id(request)
        xd = Measurement()
        xd.user_id = user_id
        xd.deserialize(request.json)

        errors = blood_repo.add(xd)
        if errors:
            return abort(400)
        return jsonify(dict()), 201

    pulse = request.form['pulse']
    systolic = request.form['systolic']
    diastolic = request.form['diastolic']
    date = request.form['date']
    time = request.form['time']

    m = Measurement()
    m.pulse = pulse
    m.systolic = systolic
    m.diastolic = diastolic
    m.date = datetime.datetime.combine(parser.parse(date), parser.parse(time).time())
    m.user_id = session['user_id']

    errors = blood_repo.add(m)
    if errors:
        for e in errors:
            flash(e.message)
        return redirect(url_for('offer.create'))

    flash("Dodano pomiar", "alert-sucess")
    return redirect(url_for('blood.index'))


@mod_blood.route('/<id>', methods=['GET'])
@requires_sign_in()
def edit_form(id):
    if is_rest_call(request):
        m = blood_repo.get_by_id(id)
        return jsonify(m.serialize()), 200
    m = blood_repo.get_by_id(id)
    return render_template('blood/edit.html', m=m)


@mod_blood.route('/<id>', methods=['POST'])
@requires_sign_in()
def edit_post(id):
    if is_rest_call(request):
        m = blood_repo.get_by_id(id)
        m.deserialize(request.json)

        errors = blood_repo.update(m)
        if errors:
            return abort(400)
        return jsonify(dict({"status" : "edited"})), 200

    pulse = request.form['pulse']
    systolic = request.form['systolic']
    diastolic = request.form['diastolic']
    date = request.form['date']
    time = request.form['time']

    m = blood_repo.get_by_id(id)
    m.pulse = pulse
    m.systolic = systolic
    m.diastolic = diastolic
    m.date = datetime.datetime.combine(parser.parse(date), parser.parse(time).time())

    blood_repo.update(m)
    flash("Zmodyfikowano", "alert-info")
    return redirect(url_for('blood.index'))


@mod_blood.route('/delete/<id>', methods=['GET'])
@requires_sign_in()
def delete_form(id):
    m = blood_repo.get_by_id(id)
    return render_template('blood/delete.html', m=m)


@mod_blood.route('/delete/<id>', methods=['POST'])
@requires_sign_in()
def delete_post(id):
    if is_rest_call(request):
        m = blood_repo.get_by_id(id)

        errors = blood_repo.delete(m)
        if errors:
            return abort(400)
        return jsonify(dict({"status" : "removed"})), 200
    blood_repo.delete(blood_repo.get_by_id(id))
    return redirect(url_for('blood.index'))
