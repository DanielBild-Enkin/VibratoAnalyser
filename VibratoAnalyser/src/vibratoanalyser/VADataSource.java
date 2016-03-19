package vibratoanalyser;

public interface VADataSource {
	// Read bytes from the data source into `vaData`. Returns the
	// number of bytes read. A negative number indicates that the data
	// source has been exhausted.
	public int readInto(byte[] vaData);
}
	
