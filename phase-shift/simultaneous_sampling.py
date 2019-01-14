"""
	In this example phase shift is obtained by
	dropping several samples of one of the signals, 
	it simulates signal aquisition delay in real world.
	Phase delay is used then to detect signal source position.

	Author: Sergey Royz
	Date: 14 Jan 2019	
"""
import matplotlib.pyplot as pp
import numpy as np
from math import sin, cos, pi
from scipy.fftpack import fft, ifft
from scipy import absolute, angle

dropped_samples = 3

N = 64
fd = 2000 # Hz
f = 200 # Hz

t = np.arange(.0, 1.0 * N / fd, 1.0 / fd)
x1 = np.vectorize(sin)(2.0 * pi * f * t)

x2 = x1[dropped_samples:]
x1 = x1[:-dropped_samples]
t = t[:-dropped_samples]

s1 = fft(x1)

k = 6
phase_shift = angle(fft(x2)[k]) - angle(s1[k])
print phase_shift
dt = phase_shift / (2.0 * pi * f)
print "Estimated delay %f" % dt
print "Real delay %f" % (1.0 * dropped_samples / fd)


pp.subplot(211)
pp.plot(t, x1)
pp.plot(t, x2)

pp.subplot(212)
pp.stem(absolute(s1))
pp.show()

