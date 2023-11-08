package com.DoctorImage



import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.stream.IntStream;

public class DoctorImage {
    public static void writeBMP(BufferedImage image, String filePath) {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (OutputStream os = new FileOutputStream(file)) {
                width = image.getWidth();
                height = image.getHeight();

                writeBMPHeader(os, image);
                writePixelData(os, image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static int width;
    static int height;
    private static void writeBMPHeader(OutputStream os, BufferedImage image) throws IOException {
        byte[] header = new byte[54]; // 14 (file header) + 40 (DIB header)

        // BMP File Header (14 bytes)
        header[0] = 0x42; // 'B'
        header[1] = 0x4D; // 'M'

        // Total file size
        int fileSize = 14 + 40 + (width * height * 3); // BMP header + DIB header + pixel data
        writeLittleEndian(header, fileSize, 2, 2);

        // Data offset
        int dataOffset = 14 + 40; // BMP header + DIB header
        writeLittleEndian(header, dataOffset, 10, 4);

        // DIB Header (40 bytes)
        writeLittleEndian(header, 40, 14, 4);
        writeLittleEndian(header, width, 18, 4);
        writeLittleEndian(header, height, 22, 4);
        writeLittleEndian(header, 1, 26, 2); // Planes (1)
        writeLittleEndian(header, 24, 28, 2); // Bits per pixel (24 for RGB)
        writeLittleEndian(header, 2835, 38, 4); // Horizontal and vertical resolution (2835 DPI)
        os.write(header);
    }

    private static void writeLittleEndian(byte[] array, int value, int offset, int bytes) {
        for (int i = 0; i < bytes; i++) {
            array[offset + i] = (byte) (value & 0xFF);
            value >>= 8;
        }
    }
    public static void writePixelData(OutputStream os, BufferedImage image) throws IOException {
        long startTime = System.nanoTime();
        int bytesPerPixel = 3;
        int rowSize = width * bytesPerPixel;
        int[] intData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        byte[] imageData = new byte[rowSize * height];

        IntStream.range(0, height)
                .parallel()
                .forEach(y -> {
                    int rowOffset = y * width;
                    for (int x = 0; x < width; x++) {
                        int pixelValue = intData[rowOffset + x];
                        int byteArrayIndex = (rowOffset + x) * 3;
                        imageData[byteArrayIndex] = (byte) pixelValue;
                        imageData[byteArrayIndex+1] = (byte) (pixelValue >> 8);
                        imageData[byteArrayIndex + 2] = (byte) (pixelValue >> 16);

                    }
                });

        os.write(imageData);

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        System.out.println("PixelData #: "+ imageData.length/3 + "pixels :RGB(write) " + executionTime + " ns");
    }
}
