package circular.framework;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author êÁót ìNék
 * @version $Name:  $
 */
class Native2AsciiInputStream extends FilterInputStream {

	InputStreamReader reader;

	int current;

	int status;

	int markedCurrent;

	int markedStatus;

	Native2AsciiInputStream(InputStream in) {
		super(in);
		reader = new InputStreamReader(in);
	}

	Native2AsciiInputStream(InputStream in, String charsetName)
		throws UnsupportedEncodingException {
		super(in);
		reader = new InputStreamReader(in, charsetName);
	}

	@Override
	public int read() throws IOException {
		switch (status) {
		case 0:
			current = reader.read();
			if (current <= 0x00ff) return current;
			status = 1;
			return '\\';
		case 1:
			status = 2;
			return 'u';
		default:
			int utf = (current >> (4 * (5 - status))) & 0x000f;
			status++;
			if (status == 6) status = 0;
			return (utf < 10 ? utf + '0' : utf - 10 + 'a');
		}
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		int count = 0;
		int i = 0;
		for (; off < len; off++) {
			i = read();
			if (i == -1) break;
			b[off] = (byte) i;
			count++;
		}
		if (i == -1 && count == 0) return -1;
		return count;
	}

	@Override
	public long skip(long n) throws IOException {
		long l = 0;
		int i = 0;
		for (; l < n; l++) {
			i = read();
			if (i == -1) break;
		}
		if (i == -1 && l == 0) return -1;
		return l;
	}

	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
		markedCurrent = current;
		markedStatus = status;
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
		current = markedCurrent;
		status = markedStatus;
	}
}
