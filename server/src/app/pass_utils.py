import typing
import hashlib
import random
import binascii
import base64


class PasswordUtil(object):
    _SHA_ITERATIONS = 10 ** 4

    def generate_salt(self) -> str:
        salt = random.SystemRandom().getrandbits(8 * 64)
        binary_salt = int.to_bytes(salt, 64, 'little')
        return base64.encodebytes(binary_salt).decode('utf8', errors='strict')

    def hash_password(self, plain_password: str, salt: str) -> typing.Tuple[str, str]:
        binary_string = bytes(plain_password, encoding='utf8', errors='strict')
        binary_salt = base64.decodebytes(salt.encode('utf8', errors='strict'))

        dk = hashlib.pbkdf2_hmac('sha256', binary_string, binary_salt, self._SHA_ITERATIONS)

        hash_base64 = base64.encodebytes(dk)

        return hash_base64.decode('utf8', errors='strict')

