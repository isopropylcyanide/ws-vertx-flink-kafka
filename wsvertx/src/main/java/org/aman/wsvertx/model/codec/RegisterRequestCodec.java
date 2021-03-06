package org.aman.wsvertx.model.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.aman.wsvertx.model.payload.RegisterRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;

public class RegisterRequestCodec implements MessageCodec<RegisterRequest, RegisterRequest> {

	private static final Logger logger = Logger.getLogger(RegisterRequestCodec.class);

	@Override
	public void encodeToWire(Buffer buffer, RegisterRequest registerRequest) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String jsonToStr = ow.writeValueAsString(registerRequest);
			int length = jsonToStr.getBytes().length;
			buffer.appendInt(length);
			buffer.appendString(jsonToStr);
		} catch (JsonProcessingException e) {
			logger.error("Error encoding [" + registerRequest + "] from " + this.name());
		}
	}

	@Override
	public RegisterRequest decodeFromWire(int position, Buffer buffer) {
		int length = buffer.getInt(position);
		// Get JSON string by it`s length
		// Jump 4 because getInt() == 4 bytes
		String jsonStr = buffer.getString(position += 4, position += length);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(new StringReader(jsonStr), RegisterRequest.class);
		} catch (IOException e) {
			logger.error("Error decoding [" + jsonStr + "] to " + this.name());
		}
		return null;
	}

	@Override
	public RegisterRequest transform(RegisterRequest registerRequest) {
		return registerRequest;
	}

	@Override
	public String name() {
		return this.getClass().getName();
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}
}
