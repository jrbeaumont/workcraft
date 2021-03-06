package org.workcraft.plugins.mpsat.commands;

import java.util.ArrayList;
import java.util.Collection;

import org.workcraft.Framework;
import org.workcraft.commands.AbstractVerificationCommand;
import org.workcraft.dom.references.ReferenceHelper;
import org.workcraft.plugins.mpsat.MpsatCombinedChainResultHandler;
import org.workcraft.plugins.mpsat.MpsatParameters;
import org.workcraft.plugins.mpsat.MpsatUtils;
import org.workcraft.plugins.mpsat.tasks.MpsatCombinedChainResult;
import org.workcraft.plugins.mpsat.tasks.MpsatCombinedChainTask;
import org.workcraft.plugins.stg.Mutex;
import org.workcraft.plugins.stg.MutexUtils;
import org.workcraft.plugins.stg.Stg;
import org.workcraft.plugins.stg.StgModel;
import org.workcraft.plugins.stg.StgPlace;
import org.workcraft.tasks.Result;
import org.workcraft.tasks.TaskManager;
import org.workcraft.util.DialogUtils;
import org.workcraft.workspace.WorkspaceEntry;
import org.workcraft.workspace.WorkspaceUtils;

public class MpsatMutexImplementabilityVerificationCommand extends AbstractVerificationCommand {

    @Override
    public String getDisplayName() {
        return "Mutex place implementability [MPSat]";
    }

    @Override
    public boolean isApplicableTo(WorkspaceEntry we) {
        return WorkspaceUtils.isApplicable(we, StgModel.class);
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public Position getPosition() {
        return Position.TOP;
    }

    @Override
    public void run(WorkspaceEntry we) {
        queueVerification(we);
    }

    @Override
    public Boolean execute(WorkspaceEntry we) {
        MpsatCombinedChainResultHandler monitor = queueVerification(we);
        Result<? extends MpsatCombinedChainResult> result = null;
        if (monitor != null) {
            result = monitor.waitResult();
        }
        return MpsatUtils.getCombinedChainOutcome(result);
    }

    private MpsatCombinedChainResultHandler queueVerification(WorkspaceEntry we) {
        MpsatCombinedChainResultHandler monitor = null;
        final Stg stg = WorkspaceUtils.getAs(we, Stg.class);
        if (structuralCheck(stg)) {
            Framework framework = Framework.getInstance();
            TaskManager manager = framework.getTaskManager();
            Collection<Mutex> mutexes = MutexUtils.getMutexes(stg);
            MutexUtils.logInfoPossiblyImplementableMutex(mutexes);
            final ArrayList<MpsatParameters> settingsList = getMutexImplementabilitySettings(mutexes);
            MpsatCombinedChainTask task = new MpsatCombinedChainTask(we, settingsList);
            String description = MpsatUtils.getToolchainDescription(we.getTitle());
            monitor = new MpsatCombinedChainResultHandler(task, mutexes);
            manager.queue(task, description, monitor);
        }
        return monitor;
    }

    private boolean structuralCheck(Stg stg) {
        Collection<StgPlace> mutexPlaces = stg.getMutexPlaces();
        if (mutexPlaces.isEmpty()) {
            DialogUtils.showError("No mutex places found to check implementability.");
            return false;
        }
        final ArrayList<StgPlace> problematicPlaces = new ArrayList<>();
        for (StgPlace place: mutexPlaces) {
            Mutex mutex = MutexUtils.getMutex(stg, place);
            if (mutex == null) {
                problematicPlaces.add(place);
            }
        }
        if (!problematicPlaces.isEmpty()) {
            String problematicPlacesString = ReferenceHelper.getNodesAsString(stg, (Collection) problematicPlaces, 50);
            DialogUtils.showError("A mutex place must precede a pair of\n" +
                    "non-input transitions, each with a single trigger.\n\n" +
                    "Problematic places are:" +
                    (problematicPlacesString.length() > 30 ? "\n" : " ") +
                    problematicPlacesString);
            return false;
        }
        return true;
    }

    private ArrayList<MpsatParameters> getMutexImplementabilitySettings(Collection<Mutex> mutexes) {
        final ArrayList<MpsatParameters> settingsList = new ArrayList<>();
        for (Mutex mutex: mutexes) {
            settingsList.add(MpsatParameters.getMutexImplementabilitySettings(mutex));
        }
        return settingsList;
    }

}
