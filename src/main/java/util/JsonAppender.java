package util;

import com.google.gson.Gson;

import java.io.*;

public final class JsonAppender extends Writer {

    private final BufferedWriter writer;
    private final char terminator;

    private boolean isAboutToWrite = true;

    private JsonAppender(final BufferedWriter writer, final char terminator) {
        this.writer = writer;
        this.terminator = terminator;
    }

    public static Writer appendAtEnd(final RandomAccessFile randomAccessFile)
            throws IOException {
        long pos = randomAccessFile.length() - 1;
        char terminator = '\u0000';
        outer_whitespace:
        for ( ; pos >= 0; pos-- ) {
            randomAccessFile.seek(pos);
            final char ch = (char) randomAccessFile.readByte();
            switch ( ch ) {
// @formatter:off
                case ' ': case '\r': case '\n': case '\t':
// @formatter:on
                    continue;
// @formatter:off
                case ']': case '}':
// @formatter:on
                    terminator = ch;
                    break outer_whitespace;
                default:
                    throw new IOException("Unexpected " + ch + " at " + pos);
            }
        }
        if ( pos < 0 ) {
            throw new IOException("No object or array begin found");
        }
        inner_whitespace:
        for ( pos -= 1; pos >= 0; pos-- ) {
            randomAccessFile.seek(pos);
            final char ch = (char) randomAccessFile.readByte();
            switch ( ch ) {
// @formatter:off
                case ' ': case '\r': case '\n': case '\t':
// @formatter:on
                    continue;
// @formatter:off
                case '}': case ']':
                case '\"':
                case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                case 'e': // for both true and false
                case 'l': // for null
// @formatter:on
                    break inner_whitespace;
                default:
                    throw new IOException("Unexpected " + ch + " at " + pos);
            }
        }
        return new JsonAppender(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(randomAccessFile.getFD()))), terminator);
    }

    @Override
    public void write(final char[] buffer, final int offset, final int length)
            throws IOException {
        if ( isAboutToWrite ) {
            isAboutToWrite = false;
            writer.write(',');
        }
        writer.write(buffer, offset, length);
    }

    @Override
    public void flush()
            throws IOException {
        writer.flush();
    }

    @Override
    public void close()
            throws IOException {
        writer.write(terminator);
        writer.close();
    }

}