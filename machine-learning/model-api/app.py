import tensorflow as tf
import numpy as np

from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/recommendations/<vac_1>', methods=['GET'])
def recommendations(vac_1):
    labels = ["AZ", "Sinovac", "Sinopharm", "Pfizer", "Moderna", "Janssen"]
    vac_1 = vac_1.lower()
    label_lower = [label.lower() for label in labels]

    if vac_1.isalpha() and vac_1 in label_lower:
        interpreter = tf.lite.Interpreter(model_path="converted_model.tflite")
        interpreter.allocate_tensors()

        # get input and output tensors
        input_details = interpreter.get_input_details()
        output_details = interpreter.get_output_details()

        interpreter.set_tensor(input_details[0]['index'], np.array([vac_1]))

        interpreter.invoke()

        output_data = interpreter.get_tensor(output_details[1]['index'])
        vaccines = [x.decode() for x in output_data[0]]

        dataJsonified = jsonify({
            "recommendations": vaccines,
            "message": "Data berhasil didapatkan"
        })
        return dataJsonified
    else:
        return jsonify({"message": "Masukkan data dengan benar"})

if __name__ == '__main__':
    app.run()
