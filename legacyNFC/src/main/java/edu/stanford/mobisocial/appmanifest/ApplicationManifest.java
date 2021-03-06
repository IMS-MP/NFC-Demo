package edu.stanford.mobisocial.appmanifest;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.mobisocial.appmanifest.platforms.ParsedPlatformReference;
import edu.stanford.mobisocial.appmanifest.platforms.PlatformReference;

public class ApplicationManifest {
	public static final int MIME_MAGIC_NUMBER = 0x41504d46; // 'APMF'
	public static final String MIME_TYPE = "application/vnd.mobisocial-appmanifest";
	public static final int PLATFORM_WEB_GET = 0x57454247; // 'WEBG'
	public static final int PLATFORM_WEB_INLINE = 0x57454249; // 'WEBI'
	public static final int PLATFORM_ANDROID_PACKAGE = 0x414e4450; // 'ANDP'
	//public static final int PLATFORM_ANDROID_INTENT = 0x414E4401; // {'A', 'N', 'D', 0x01}
	public static final int PLATFORM_SYMBIAN_PACKAGE = 0x53594D00; // {'S', 'Y', 'M', 0x00}
	public static final int PLATFORM_IOS_PACKAGE = 0x694F5300; // {'i', 'O', 'S', 0x00i
	public static final int PLATFORM_WINDOWS_PHONE_PACKAGE = 0x57503700; // {'W', 'P', '7', 0x00}
	public static final int PLATFORM_WEBOS_PACKAGE = 0x774f5300; // {'w', 'O', 'S', 0x00}
	public static final int PLATFORM_JAVA_JAR = 0x4a415600; // {'J', 'A', 'V', 0x00}
	
	public static int MODALITY_UNSPECIFIED = 0x00;
	public static int MODALITY_HEADLESS = 0x01;
	public static int MODALITY_PHONE = 0x02;
	public static int MODALITY_COMPUTER = 0x03;
	public static int MODALITY_TELEVISION = 0x04;
	public static int MODALITY_TABLET = 0x05;
	public static int MODALITY_PROJECTOR = 0x06;
	
	String mName;
	List<PlatformReference> mPlatformReferences;
	
	public String getName() {
		return mName;
	}
	
	public List<PlatformReference> getPlatformReferences() {
		return mPlatformReferences;
	}
	
	public byte[] toByteArray() {
		// TODO: allocate correct size
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.putInt(MIME_MAGIC_NUMBER);
		buffer.putInt(mName.length());
		buffer.put(mName.getBytes());
		buffer.putInt(mPlatformReferences.size());
		for (PlatformReference platform : mPlatformReferences) {
			byte[] appReference = platform.getAppReference();
			buffer.putInt(platform.getPlatformIdentifier());
			buffer.putInt(platform.getPlatformVersion());
			buffer.putInt(platform.getDeviceModality());
			buffer.putInt(appReference.length);
			buffer.put(appReference);
		}
		int length = buffer.position();
		buffer.position(0);
		byte[] manifest = new byte[length];
		buffer.get(manifest, 0, length);
		return manifest;
	}
	
	public ApplicationManifest(byte[] source) {
		ByteBuffer buffer = ByteBuffer.wrap(source);
		int magicNumber = buffer.getInt();
		if (magicNumber != MIME_MAGIC_NUMBER) {
			throw new IllegalArgumentException("Magic number not found.");
		}
		int nameLength = buffer.getInt();
		byte[] nameBytes = new byte[nameLength];
		buffer.get(nameBytes);
		mName = new String(nameBytes);

		int platformCount = buffer.getInt();
		mPlatformReferences = new ArrayList<PlatformReference>(platformCount);
		for (int i = 0; i < platformCount; i++) {
			int platformIdentifier = buffer.getInt();
			int platformVersion = buffer.getInt();
			int deviceModality = buffer.getInt();
			int appReferenceLength = buffer.getInt();
			byte[] appReference = new byte[appReferenceLength];
			buffer.get(appReference);

			PlatformReference platformReference = new ParsedPlatformReference(
					platformIdentifier, platformVersion, deviceModality, appReference);
			mPlatformReferences.add(platformReference);
		}
	}
	
	private ApplicationManifest() {}
	
	public static class Builder {
		ApplicationManifest mApplicationManifest;
		
		public Builder() {
			mApplicationManifest = new ApplicationManifest();
			mApplicationManifest.mPlatformReferences = new LinkedList<PlatformReference>();
		}
		
		public ApplicationManifest create() {
			return mApplicationManifest;
		}
		
		public Builder setName(String name) {
			mApplicationManifest.mName = name;
			return this;
		}
		
		public Builder addPlatformReference(PlatformReference reference) {
			mApplicationManifest.mPlatformReferences.add(reference);
			return this;
		}
	}
}
