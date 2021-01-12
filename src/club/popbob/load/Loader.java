package club.popbob.load;

import club.popbob.Cheat;
import club.popbob.Config;
import club.popbob.Main;
import club.popbob.web.Reader;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.EnumSet;
import java.util.List;

import static com.sun.jna.platform.win32.WinNT.*;

public class Loader {
    private final Cheat thisCheat;
    private static final Config config = Main.config;
    public Loader(Cheat cheat) throws IOException {
        var cheatUpdates = config.getConfig().cheatUpdates;
        thisCheat = cheat;

        String fileType = thisCheat.file.split("\\.")[1];
        String cheatFile = System.getenv("APPDATA") + "\\cfe\\" + thisCheat.display_name + "." + thisCheat.file.split("\\.")[1];
        System.out.println(cheatFile);
        //local cheat update time is different than api
        //also why does java not have easy way to use empty string if null
        if (!(cheatUpdates.get(cheat.display_name) == null ? "" : cheatUpdates.get(cheat.display_name)).equals(cheat.updated)) {
            System.out.println(cheat.display_name);
            cheatUpdates.put(cheat.display_name, cheat.updated);
            try {
                try (InputStream in = new URL("https://popbob.club/binaries/" + thisCheat.file).openStream()) {
                    if (Files.exists(Paths.get(cheatFile)))
                        Files.delete(Paths.get(cheatFile));
                    Files.copy(in, Paths.get(cheatFile));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(fileType.equalsIgnoreCase("dll")) {
            try {
                loadDLL(cheatFile);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else if(fileType.equalsIgnoreCase("exe")) {
            loadExe(cheatFile);
        } else if(fileType.equalsIgnoreCase("jar")) {
            loadJar(cheatFile);
        }
    }

    public void loadDLL(String dll) throws Exception {
        Tlhelp32.PROCESSENTRY32 processInfo = new Tlhelp32.PROCESSENTRY32.ByReference();
        WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));

        AclFileAttributeView view = Files.getFileAttributeView(Paths.get(dll), AclFileAttributeView.class);
        UserPrincipal userPrincipal = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByGroupName("ALL APPLICATION PACKAGES");
        AclEntry entry = AclEntry.newBuilder()
                .setType(AclEntryType.ALLOW)
                .setPrincipal(userPrincipal)
                .setPermissions(EnumSet.allOf(AclEntryPermission.class))
                .build();
        List<AclEntry> entries = view.getAcl();
        entries.add(entry);
        view.setAcl(entries);

        WinDef.DWORD pid = null;
        Kernel32.INSTANCE.Process32First(snapshot, processInfo);
        if(Native.toString(processInfo.szExeFile).contains("Minecraft.Windows.exe")) {
            System.out.println(processInfo.szExeFile);
            Kernel32.INSTANCE.CloseHandle(snapshot);
            pid = processInfo.th32ProcessID;
        }

        while(Kernel32.INSTANCE.Process32Next(snapshot, processInfo)) {
            if(Native.toString(processInfo.szExeFile).contains("Minecraft.Windows.exe")) {
                com.sun.jna.platform.win32.Kernel32.INSTANCE.CloseHandle(snapshot);
                pid = processInfo.th32ProcessID;
            }
        }

        WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_ALL_ACCESS, false, Integer.parseInt(String.valueOf(pid)));
        SECURITY_ATTRIBUTES security_attributes = new SECURITY_ATTRIBUTES();
        Pointer remote = Kernel32.INSTANCE.VirtualAllocEx(process, Pointer.NULL, new SIZE_T(dll.length() + 1), MEM_RESERVE | MEM_COMMIT, PAGE_READWRITE);
        // LoadLibraryA is ordinal 969 in kernel32.dll (link /dump /exports kernel32.dll)
        Pointer LoadLibraryA = Kernel32.INSTANCE.GetProcAddress(Kernel32.INSTANCE.GetModuleHandle("kernel32.dll"), 969);
        Pointer dllptr = new Memory(dll.length() + 1);
        dllptr.setString(0, dll);
        Kernel32.INSTANCE.WriteProcessMemory(process, remote, dllptr, dll.length(), null);
        Kernel32.INSTANCE.CreateRemoteThread(process, security_attributes, 0, LoadLibraryA, remote, 0, new DWORDByReference());
    }

    public void loadExe(String exe) {

    }

    public void loadJar(String jar) throws IOException {
        for(String lib : thisCheat.libs) {
            try {
                try (InputStream in = new URL("https://popbob.club/binaries/libraries/" + lib).openStream()) {
                    String path = System.getenv("APPDATA") + "\\cfe\\libs\\" + lib;
                    if (!Files.exists(Paths.get(path)))
                        Files.copy(in, Paths.get(path));
                    else
                        in.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        Runtime.getRuntime().exec("java -jar " + jar);
    }
}
