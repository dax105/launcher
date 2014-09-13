package com.jboudny.launcher;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * provides OS-specific utilities
 */
public class OSUtils {

	// ~ Enumerations
	// ---------------------------------------------------------------------------------------------------

	public enum Platform {
		WIN32, WIN64, MAC32, MAC64, LINUX32, LINUX64, UNKNOWN32, UNKNOWN64;
	}

	// ~ Methods
	// --------------------------------------------------------------------------------------------------------

	public static boolean isProcessRunning(Process p) {
		try {
	        p.exitValue();
	        return false;
	    } catch (Exception e) {
	        return true;
	    }
	}
	
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	
	public static int getFreeRam() {
        Platform p = OSUtils.getPlatform();
        
        if(p == Platform.WIN64) {
        	return OSUtils.getFreeRamWindows();
        } else if(p == Platform.WIN32 || p == Platform.LINUX32) {
        	return 1024;
        } else if(p == Platform.LINUX64) {
        	return OSUtils.getFreeRamLinux();
        }
        
		return 1024;
	}
	
	private static int getFreeRamWindows() {
		int mb = 1024 * 1024; 
		 
		MEMORYSTATUSEX mse = new MEMORYSTATUSEX();
		Kernel32.INSTANCE.GlobalMemoryStatusEx(mse);
		mse.read();
		
		/*System.out.println("-----------------------------");
		System.out.println("AEV " + mse.ullAvailExtendedVirtual / mb);
		System.out.println("APF " + mse.ullAvailPageFile / mb);
		System.out.println("AP " + mse.ullAvailPhys / mb);
		System.out.println("AV " + mse.ullAvailVirtual / mb);
		System.out.println("TPF " + mse.ullTotalPageFile / mb);
		System.out.println("TP " + mse.ullTotalPhys / mb);
		System.out.println("TV " + mse.ullTotalVirtual / mb);
		System.out.println("-----------------------------");*/
		
		return (int)(mse.ullAvailPhys / mb);
	}
	
	private static int getFreeRamLinux() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));

			String ln;
			while ((ln = br.readLine()) != null) {
				String[] parts = ln.split("\\s+");
				
				if(parts[0].trim().toLowerCase().startsWith("memfree")) {
					br.close();
					return (Integer.parseInt(parts[1].trim().substring(0, parts[1].trim().indexOf(' '))) / 1024);
				}
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 1024;
	}

	public static String getJVMPath() {
		return System.getProperty("java.home") + File.separator + "bin"
				+ File.separator + "java";
	}

	public static Platform getPlatform() {

		String bitage = System.getProperty("sun.arch.data.model");
		boolean bit64 = false;
		if (bitage.equals("32")) {
			bit64 = false;
		} else if (bitage.equals("64")) {
			bit64 = true;
		} else {
			Logger.getGlobal().info("Unknown architecture");
		}

		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") != -1) {
			return (bit64 ? Platform.WIN64 : Platform.WIN32);
		} else if (os.indexOf("mac") != -1) {
			return (bit64 ? Platform.MAC64 : Platform.MAC32);
		} else if (os.indexOf("linux") != -1) {
			return (bit64 ? Platform.LINUX64 : Platform.LINUX32);
		} else {
			Logger.getGlobal().info("Unknown OS");
			return (bit64 ? Platform.UNKNOWN64 : Platform.UNKNOWN32);
		}
	}

	public static boolean isWindows() {
		return ((getPlatform() == Platform.WIN32) || (getPlatform() == Platform.WIN64));
	}

	public static boolean isMac() {
		return ((getPlatform() == Platform.MAC32) || (getPlatform() == Platform.MAC64));
	}

	public static boolean isLinux() {
		return ((getPlatform() == Platform.LINUX32) || (getPlatform() == Platform.LINUX64));
	}

	/**
	 * Returns the correct userDataFolder for the given application name.
	 */
	public static String userDataFolder(final String applicationName) {

		// default
		String folder = "." + File.separator;

		if (isMac()) {
			folder = System.getProperty("user.home") + File.separator
					+ "Library" + File.separator + "Application Support";
		} else if (isWindows()) {

			Map<String, Object> options = new HashMap<>();
			options.put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
			options.put(Library.OPTION_FUNCTION_MAPPER,
					W32APIFunctionMapper.UNICODE);

			HWND hwndOwner = null;
			int nFolder = Shell32.CSIDL_LOCAL_APPDATA;
			HANDLE hToken = null;
			int dwFlags = Shell32.SHGFP_TYPE_CURRENT;
			char pszPath[] = new char[Shell32.MAX_PATH];
			Shell32 instance = (Shell32) Native.loadLibrary("shell32",
					Shell32.class, options);
			int hResult = instance.SHGetFolderPath(hwndOwner, nFolder, hToken,
					dwFlags, pszPath);
			if (Shell32.S_OK == hResult) {

				String path = new String(pszPath);
				int len = path.indexOf('\0');
				folder = path.substring(0, len);
			} else {
				System.err.println("Error: " + hResult);
			}
		} else if (isLinux()) {
			folder = System.getProperty("user.home");
		}

		folder = folder + File.separator + applicationName + File.separator;

		return folder;
	}

	// ~ Inner Interfaces
	// -----------------------------------------------------------------------------------------------

	private static interface Shell32 extends Library {

		public static final int MAX_PATH = 260;
		public static final int CSIDL_LOCAL_APPDATA = 0x001c;
		public static final int SHGFP_TYPE_CURRENT = 0;
		@SuppressWarnings("unused")
		public static final int SHGFP_TYPE_DEFAULT = 1;
		public static final int S_OK = 0;

		/**
		 * see http://msdn.microsoft.com/en-us/library/bb762181(VS.85).aspx
		 *
		 * HRESULT SHGetFolderPath( HWND hwndOwner, int nFolder, HANDLE hToken,
		 * DWORD dwFlags, LPTSTR pszPath);
		 */
		public int SHGetFolderPath(final HWND hwndOwner, final int nFolder,
				final HANDLE hToken, final int dwFlags, final char pszPath[]);
	}
	
    public interface Kernel32 extends StdCallLibrary {
        boolean GlobalMemoryStatusEx(MEMORYSTATUSEX p);

        Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("kernel32",Kernel32.class);
    }

    public static final class MEMORYSTATUSEX extends Structure {
    	
        public int dwLength = size();
        public int dwMemoryLoad;
        public long ullTotalPhys;
        public long ullAvailPhys;
        public long ullTotalPageFile;
        public long ullAvailPageFile;
        public long ullTotalVirtual;
        public long ullAvailVirtual;
        public long ullAvailExtendedVirtual;
        
		@SuppressWarnings("rawtypes")
		@Override
		protected List getFieldOrder() {
			ArrayList<String> names = new ArrayList<String>();
			names.add("dwLength");
    		names.add("dwMemoryLoad");
    		names.add("ullTotalPhys");
    		names.add("ullAvailPhys");
    		names.add("ullTotalPageFile");
    		names.add("ullAvailPageFile");
    		names.add("ullTotalVirtual");
    		names.add("ullAvailVirtual");
    		names.add("ullAvailExtendedVirtual");
    		
			return names;
		}
    }

	// ~ Inner Classes
	// --------------------------------------------------------------------------------------------------

	private static class HANDLE extends PointerType {
	}

	private static class HWND extends HANDLE {
	}
}