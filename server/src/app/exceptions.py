class BloodException(Exception):
    def serialize(self):
        return {
            'error': self.args
        }