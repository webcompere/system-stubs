package uk.org.webcompere.systemstubs.security;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * A {@code NoExitSecurityManager} throws a {@link AbortExecutionException}
 * exception whenever {@link #checkExit(int)} is called. All other method
 * calls are delegated to the original security manager.
 * @since 1.0.0
 */
public class NoExitSecurityManager extends SecurityManager {
    private final SecurityManager originalSecurityManager;
    private Integer statusOfFirstExitCall = null;

    public NoExitSecurityManager(SecurityManager originalSecurityManager) {
        this.originalSecurityManager = originalSecurityManager;
    }

    @Override
    public void checkExit(int status) {
        if (statusOfFirstExitCall == null) {
            statusOfFirstExitCall = status;
        }
        throw new AbortExecutionException();
    }

    boolean isCheckExitCalled() {
        return statusOfFirstExitCall != null;
    }

    /**
     * Validate that a system exit was called. Throws an AssertionError if not
     * @return the exit code
     */
    public int checkSystemExit() {
        if (isCheckExitCalled()) {
            return getStatusOfFirstCheckExitCall();
        }

        throw new AssertionError("System.exit has not been called.");
    }

    /**
     * Get the exit code if there was one
     * @return the exit code
     */
    public Integer getExitCode() {
        if (isCheckExitCalled()) {
            return statusOfFirstExitCall;
        }
        return null;
    }

    private int getStatusOfFirstCheckExitCall() {
        if (isCheckExitCalled()) {
            return statusOfFirstExitCall;
        }
        throw new IllegalStateException("checkExit(int) has not been called.");
    }

    @Override
    public Object getSecurityContext() {
        return (originalSecurityManager == null) ? super.getSecurityContext()
            : originalSecurityManager.getSecurityContext();
    }

    @Override
    public void checkPermission(Permission perm) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPermission(perm);
        }
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPermission(perm, context);
        }
    }

    @Override
    public void checkCreateClassLoader() {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkCreateClassLoader();
        }
    }

    @Override
    public void checkAccess(Thread t) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkAccess(t);
        }
    }

    @Override
    public void checkAccess(ThreadGroup g) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkAccess(g);
        }
    }

    @Override
    public void checkExec(String cmd) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkExec(cmd);
        }
    }

    @Override
    public void checkLink(String lib) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkLink(lib);
        }
    }

    @Override
    public void checkRead(FileDescriptor fd) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkRead(fd);
        }
    }

    @Override
    public void checkRead(String file) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkRead(file);
        }
    }

    @Override
    public void checkRead(String file, Object context) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkRead(file, context);
        }
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkWrite(fd);
        }
    }

    @Override
    public void checkWrite(String file) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkWrite(file);
        }
    }

    @Override
    public void checkDelete(String file) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkDelete(file);
        }
    }

    @Override
    public void checkConnect(String host, int port) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkConnect(host, port);
        }
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkConnect(host, port, context);
        }
    }

    @Override
    public void checkListen(int port) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkListen(port);
        }
    }

    @Override
    public void checkAccept(String host, int port) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkAccept(host, port);
        }
    }

    @Override
    public void checkMulticast(InetAddress maddr) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkMulticast(maddr);
        }
    }

    @Override
    public void checkMulticast(InetAddress maddr, byte ttl) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkMulticast(maddr, ttl);
        }
    }

    @Override
    public void checkPropertiesAccess() {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPropertiesAccess();
        }
    }

    @Override
    public void checkPropertyAccess(String key) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPropertyAccess(key);
        }
    }

    @Override
    public void checkPrintJobAccess() {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPrintJobAccess();
        }
    }

    @Override
    public void checkPackageAccess(String pkg) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPackageAccess(pkg);
        }
    }

    @Override
    public void checkPackageDefinition(String pkg) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPackageDefinition(pkg);
        }
    }

    @Override
    public void checkSetFactory() {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkSetFactory();
        }
    }

    @Override
    public void checkSecurityAccess(String target) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkSecurityAccess(target);
        }
    }

    @Override
    public ThreadGroup getThreadGroup() {
        return (originalSecurityManager == null) ? super.getThreadGroup()
            : originalSecurityManager.getThreadGroup();
    }
}
