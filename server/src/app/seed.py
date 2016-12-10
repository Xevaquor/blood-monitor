import app
from app.model.entities import Measurement, User
from faker import Factory
from app.pass_utils import PasswordUtil
import random
import datetime
import dateutil
import sys
import logging
logging.basicConfig()
logging.getLogger('sqlalchemy.engine').setLevel(logging.ERROR)

fake = Factory.create('pl_PL')

AMOUNT_OF_USERS = 500
AMOUNT_OF_MEASUREMENTS = 30

pu = PasswordUtil()

app.db.session.commit()
app.db.drop_all()
app.db.create_all()


admin = User()
admin.name = 'jaro'
admin.salt = pu.generate_salt()
admin.password = pu.hash_password('jaro', admin.salt)

app.db.session.add(admin)
app.db.session.commit()

for _ in range(AMOUNT_OF_MEASUREMENTS):
    m = Measurement()
    m.systolic = random.randint(100,120)
    m.diastolic = random.randint(80,90)
    m.pulse = random.randint(60,80)
    m.date = datetime.datetime(2016,12, _ % 30+1, _ * 3 % 24, _ * 7 % 60)
    m.user_id = admin.id

    app.db.session.add(m)
    print('.', end='')


app.db.session.commit()

print('asd')
