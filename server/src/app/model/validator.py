from app.repo.user_repo import user_exists


class ValidationError(object):
    def __init__(self, msg):
        self.message = msg


class PasswordValidator(object):
    def __init__(self, pass_func, confirmation_func):
        self.pass_func = pass_func
        self.confirmation_func = confirmation_func

    def validate(self):
        errors = []
        if self.pass_func() != self.confirmation_func():
            errors += [ValidationError("Hasło i jego potwierdzenie muszą być takie same")]
        if len(self.pass_func()) < 3:
            errors += [ValidationError("Hasło musi mieć co najmniej 3 znaki")]

        return errors, len(errors) == 0


class UniqueUsernameValidator(object):
    def __init__(self, username):
        self.username_func = username

    def validate(self):
        if user_exists(self.username_func()):
            return [], True
        else:
            return [ValidationError('Użytkownik o podanym adresie e-mail już istnieje!')], False


class CombinedValidator(object):
    def __init__(self, validators):
        self.validators = validators

    def validate(self):
        errors = []
        for v in self.validators:
            e, _ = v.validate()
            errors += e
        return errors, len(errors) == 0
