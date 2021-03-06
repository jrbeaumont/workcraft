package org.workcraft.plugins.mpsat.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.workcraft.plugins.mpsat.MpsatSettings;
import org.workcraft.plugins.punf.PunfSettings;
import org.workcraft.plugins.shared.tasks.ExternalProcessResult;
import org.workcraft.plugins.shared.tasks.ExternalProcessTask;
import org.workcraft.tasks.ProgressMonitor;
import org.workcraft.tasks.Result;
import org.workcraft.tasks.Result.Outcome;
import org.workcraft.tasks.Task;
import org.workcraft.util.DialogUtils;
import org.workcraft.util.FileUtils;
import org.workcraft.util.ToolUtils;

public class MpsatTask implements Task<ExternalProcessResult> {
    public static final String FILE_NET_G = "net.g";
    // IMPORTANT: The name of output file must be mpsat.g -- this is not configurable on MPSat side.
    public static final String FILE_MPSAT_G = "mpsat.g";
    public static final String FILE_PLACES = "places.list";

    private final String[] args;
    private final File unfoldingFile;
    private final File directory;
    private final boolean tryPnml;
    private final File netFile;
    private final File placesFile;

    public MpsatTask(String[] args, File unfoldingFile, File directory) {
        this(args, unfoldingFile, directory, true, null, null);
    }

    public MpsatTask(String[] args, File unfoldingFile, File directory, boolean tryPnml) {
        this(args, unfoldingFile, directory, tryPnml, null, null);
    }

    public MpsatTask(String[] args, File unfoldingFile, File directory, boolean tryPnml, File netFile) {
        this(args, unfoldingFile, directory, tryPnml, netFile, null);
    }

    public MpsatTask(String[] args, File unfoldingFile, File directory, boolean tryPnml, File netFile, File placesFile) {
        this.args = args;
        this.unfoldingFile = unfoldingFile;
        if (directory == null) {
            // Prefix must be at least 3 symbols long.
            directory = FileUtils.createTempDirectory("mpsat-");
        }
        this.directory = directory;
        this.tryPnml = tryPnml;
        this.netFile = netFile;
        this.placesFile = placesFile;
    }

    @Override
    public Result<? extends ExternalProcessResult> run(ProgressMonitor<? super ExternalProcessResult> monitor) {
        ArrayList<String> command = new ArrayList<>();

        // Name of the executable
        String toolPrefix = MpsatSettings.getCommand();
        String toolSuffix = PunfSettings.getToolSuffix(tryPnml);
        String toolName = ToolUtils.getAbsoluteCommandWithSuffixPath(toolPrefix, toolSuffix);
        command.add(toolName);

        // Built-in arguments
        for (String arg : args) {
            command.add(arg);
        }

        // Extra arguments (should go before the file parameters)
        String extraArgs = MpsatSettings.getArgs();
        if (MpsatSettings.getAdvancedMode()) {
            String tmp = DialogUtils.showInput("Additional parameters for MPSat:", extraArgs);
            if (tmp == null) {
                return Result.cancelation();
            }
            extraArgs = tmp;
        }
        for (String arg : extraArgs.split("\\s")) {
            if (!arg.isEmpty()) {
                command.add(arg);
            }
        }

        // Input file
        if (unfoldingFile != null) {
            command.add(unfoldingFile.getAbsolutePath());
        }

        boolean printStdout = MpsatSettings.getPrintStdout();
        boolean printStderr = MpsatSettings.getPrintStderr();
        ExternalProcessTask task = new ExternalProcessTask(command, directory, printStdout, printStderr);
        Result<? extends ExternalProcessResult> res = task.run(monitor);
        if (res.getOutcome() == Outcome.SUCCESS) {
            Map<String, byte[]> fileContentMap = new HashMap<>();
            try {
                if ((netFile != null) && netFile.exists()) {
                    fileContentMap.put(FILE_NET_G, FileUtils.readAllBytes(netFile));
                }
                if ((placesFile != null) && placesFile.exists()) {
                    fileContentMap.put(FILE_PLACES, FileUtils.readAllBytes(placesFile));
                }
                File outFile = new File(directory, FILE_MPSAT_G);
                if (outFile.exists()) {
                    fileContentMap.put(FILE_MPSAT_G, FileUtils.readAllBytes(outFile));
                }
            } catch (IOException e) {
                return new Result<ExternalProcessResult>(e);
            }

            ExternalProcessResult retVal = res.getReturnValue();
            ExternalProcessResult result = new ExternalProcessResult(
                    retVal.getReturnCode(), retVal.getOutput(), retVal.getErrors(), fileContentMap);

            if (retVal.getReturnCode() < 2) {
                return Result.success(result);
            } else {
                return Result.failure(result);
            }
        }
        if (res.getOutcome() == Outcome.CANCEL) {
            return Result.cancelation();
        }
        return Result.failure(null);
    }

}
