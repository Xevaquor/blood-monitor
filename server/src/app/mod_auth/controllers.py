from flask import Blueprint, request, render_template, flash, g, session, redirect, url_for, session

from app import db

mod_auth = Blueprint('auth', __name__, url_prefix='/auth')
