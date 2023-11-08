# DoctorImage
java.awt.image.BufferedImage Writer, 24bit bmp generator. Fast (16,384px^2 in 2.1seconds)

Java file that can be included in your projects if you need to generate 24bit BMP images faster than javas native ImageIO solution. on smaller files we are about three times faster, but the larger we grow the more we shine.

Its better/faster to deal directly with the DataBufferInt as it saves alot of unessicary object creation and casting which cost cpu time.
i9-13900K can push 23fps~ writing to disk with decentssd
