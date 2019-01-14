"""
	In this example two signals are generated with
	artificailly shifted phases. Phase shift is 
	obtained in frequency domain.

	Author: Sergey Royz
	Date: 14 Jan 2019	
"""
import matplotlib.pyplot as pp
import numpy as np
from math import sin, cos, pi
from scipy.fftpack import fft, ifft
from scipy import absolute, angle

phase_shift = pi / 12

t = np.arange(.0, 2.0 * pi, pi / 32)
x1 = np.vectorize(sin)(t)
x2 = np.vectorize(sin)(t + phase_shift)

pp.subplot(311)
pp.plot(t, x1)
pp.plot(t, x2)
pp.grid(True)

s1 = fft(x1)
s2 = fft(x2)

pp.subplot(312)
pp.stem(absolute(s1))

pp.subplot(313)
pp.stem(absolute(s2))

k = 1

print angle(s2[k]) - angle(s1[k])
print pi / 12

pp.show()