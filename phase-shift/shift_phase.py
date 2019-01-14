"""
	In this example the sinusoidal wave is generated,
	then phase is shifted in frequency domain and
	the signal is reconstructed to depict phase shift.

	Author: Sergey Royz
	Date: 10 Jan 2019	
"""
import matplotlib.pyplot as pp
import numpy as np
from math import sin, cos, pi
from scipy.fftpack import fft, ifft
from scipy import absolute, angle

def rotate(x, alpha):
	r = absolute(x)
	phi = angle(x)
	return r * cos(phi + alpha) + 1j * r * sin(phi + alpha)

t = np.arange(.0, 2.0 * pi, pi / 32)
samples = np.vectorize(sin)(t)

spectrum = fft(samples)

pp.subplot(311)
pp.plot(t, samples)
pp.grid(True)
pp.subplot(312)

k = 1
spectrum[k] = rotate(spectrum[k], pi/2)
spectrum[-k] = rotate(spectrum[-k], -pi/2)
pp.stem(absolute(spectrum))
pp.subplot(313)
pp.plot(t, ifft(spectrum))
pp.grid(True)

pp.show()