package club.popbob.load;

import club.popbob.web.Reader;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;

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
    public Loader(String cheat) throws IOException {
        String dll = System.getProperty("user.dir") + "\\cheat.dll";
        String mc = "Minecraft.Windows.exe";
        WinDef.DWORD pid = null;
        Tlhelp32.PROCESSENTRY32 processInfo = new Tlhelp32.PROCESSENTRY32.ByReference();
        WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));

        try (InputStream in = new URL("https://popbob.club/" + Reader.getCheatData(cheat).file).openStream()) {
            if(Files.exists(Paths.get(dll)))
                Files.delete(Paths.get(dll));
            Files.copy(in, Paths.get(dll));
        }

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

        Kernel32.INSTANCE.Process32First(snapshot, processInfo);
        if(Native.toString(processInfo.szExeFile).contains(mc)) {
            System.out.println(processInfo.szExeFile);
            Kernel32.INSTANCE.CloseHandle(snapshot);
            pid = processInfo.th32ProcessID;
        }

        while(Kernel32.INSTANCE.Process32Next(snapshot, processInfo)) {
            if(Native.toString(processInfo.szExeFile).contains(mc)) {
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
}
