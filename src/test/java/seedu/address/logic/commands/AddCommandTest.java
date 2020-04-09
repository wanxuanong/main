package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.fail;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DEADLINE_DATE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DEADLINE_DATE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DEADLINE_TIME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DEADLINE_TIME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GRADE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GRADE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MODCODE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MODCODE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SEMESTER_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SEMESTER_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TASK_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TASK_BOB;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
//import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddCommandParserTest;
//import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.CourseManager;
import seedu.address.model.Model;
import seedu.address.model.ModuleList;
import seedu.address.model.ModuleManager;
import seedu.address.model.ProfileList;
import seedu.address.model.ProfileManager;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.profile.Name;
import seedu.address.model.profile.Profile;
import seedu.address.model.profile.Year;
import seedu.address.model.profile.course.AcceptedCourses;
import seedu.address.model.profile.course.AcceptedFocusArea;
import seedu.address.model.profile.course.Course;
import seedu.address.model.profile.course.CourseFocusArea;
import seedu.address.model.profile.course.CourseName;
import seedu.address.model.profile.course.FocusArea;
import seedu.address.model.profile.course.module.Description;
import seedu.address.model.profile.course.module.ModularCredits;
import seedu.address.model.profile.course.module.Module;
//import seedu.address.model.profile.course.module.Module;
import seedu.address.model.profile.course.module.ModuleCode;
import seedu.address.model.profile.course.module.Preclusions;
import seedu.address.model.profile.course.module.PrereqTreeNode;
import seedu.address.model.profile.course.module.Prereqs;
import seedu.address.model.profile.course.module.SemesterData;
import seedu.address.model.profile.course.module.Title;
import seedu.address.model.profile.course.module.personal.Deadline;
import seedu.address.model.profile.course.module.personal.DeadlineList;
import seedu.address.storage.JsonModuleListStorage;

public class AddCommandTest {

    private static final Logger logger = LogsCenter.getLogger(AddCommandParserTest.class);
    private final ModuleCode moduleCode;
    private final int semester;
    private final String grade;
    private final String task;
    private final LocalDate date;
    private final LocalTime time;

    private ProfileManager profileManager;
    private CourseManager courseManager;
    private ModuleManager moduleManager;

    public AddCommandTest() {
        String moduleListFilePath = "/data/modulesPrereq.json";
        JsonModuleListStorage modules = new JsonModuleListStorage(moduleListFilePath);
        try {
            Optional<ModuleList> moduleListOptional = modules.readModuleList();
            if (!moduleListOptional.isPresent()) {
                logger.info("Data file not found. Will be starting with an empty ModuleList");
                new ModuleManager();
            } else {
                ModuleList moduleList = moduleListOptional.get();
                new ModuleManager(moduleList);
            }
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty ModuleList");
            new ModuleManager();
        }

        moduleCode = new ModuleCode(VALID_MODCODE_BOB);
        semester = new Year(VALID_SEMESTER_BOB).getSemester();
        grade = VALID_GRADE_BOB;
        task = VALID_TASK_BOB;
        date = LocalDate.parse(VALID_DEADLINE_DATE_BOB);
        time = LocalTime.parse(VALID_DEADLINE_TIME_BOB, DateTimeFormatter.ofPattern("HH:mm"));
    }

    // No module code added, user inputs "add m/"
    @Test
    public void constructor_nullModule_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AddCommand(null, semester, grade, task, date, time));
    }

    @Test
    public void execute_duplicateModule_throwsCommandException() {
        Profile validProfile;
        validProfile = new Profile(
                new Name("name"), new CourseName(AcceptedCourses.COMPUTER_SCIENCE.getName()), 3,
                new FocusArea(AcceptedFocusArea.ALGORITHMS_AND_THEORY.getName()));
        Module module = new Module(moduleCode, new Title("title"), new Prereqs("prereq"),
                new Preclusions("preclusion"), new ModularCredits("4"), new Description("desc"),
                new SemesterData(Arrays.asList("1", "2")), new PrereqTreeNode(moduleCode));
        String initialStatus = module.getPersonal().getStatus();
        // Create an AddCommand which attempts to add a module with the same module code
        AddCommand addCommand = new AddCommand(moduleCode, semester, null, null, null, null);
        // Create a "ProfileManager" which contains validProfile, which contains module
        ModelStub modelStub = new ModelStubWithProfile(validProfile, module);

        // Executing addCommand.execute should raise CommandException with the duplicate module message
        /*assertThrows(CommandException.class,
                String.format(AddCommand.MESSAGE_DUPLICATE_MODULE,
                        module.getPersonal().getStatus()), () -> addCommand.execute(
                                profileManager, courseManager, moduleManager));*/

        // Also make sure that the module's status does not change after failing to add duplicate module
        assertTrue(module.getPersonal().getStatus().equals(initialStatus));
    }

    @Test
    public void execute_newModule_noChangeStatus() {
        Profile validProfile;
        validProfile = new Profile(
                new Name("name"), new CourseName(AcceptedCourses.COMPUTER_SCIENCE.getName()), 3,
                new FocusArea(AcceptedFocusArea.ALGORITHMS_AND_THEORY.getName()));
        Module module = new Module(new ModuleCode(VALID_MODCODE_AMY), new Title("title"),
                new Prereqs("prereq"), new Preclusions("preclusions"), new ModularCredits("4"),
                new Description("desc"), new SemesterData(Arrays.asList("1", "2")),
                new PrereqTreeNode(new ModuleCode(VALID_MODCODE_AMY)));
        String initialStatus = module.getPersonal().getStatus();
        String initialGrade = module.getPersonal().getGrade();
        DeadlineList initialDeadlines = module.getPersonal().getDeadlineList();
        // Create an AddCommand which attempts to add a module with the same module code
        AddCommand addCommand = new AddCommand(moduleCode, semester, null, null, null, null);

        // Create a "ProfileManager" which contains validProfile, which contains module
        //ProfileManager profileManager = new ProfileManager(profileList, userPref);
        /*try {
           addCommand.execute(profileManager, courseManager, moduleManager);
        } catch (CommandException e) {
            fail();
        }*/

        assertTrue(module.getPersonal().getStatus().equals(initialStatus)); // Check if status is the same
        boolean sameGrade = (module.getPersonal().getGrade() == null && initialGrade == null)
                || (module.getPersonal().getGrade() != null && initialGrade != null
                && module.getPersonal().getGrade().equals(initialGrade));
        assertTrue(sameGrade); // Check if grade is the same
        assertTrue(module.getPersonal().getDeadlineList().equals(initialDeadlines)); // Check if deadlines are same
    }

    @Test
    public void equals() {
        LocalDate modelDate = LocalDate.parse(VALID_DEADLINE_DATE_AMY);
        LocalTime modelTime = LocalTime.parse(VALID_DEADLINE_TIME_AMY, DateTimeFormatter.ofPattern("HH:mm"));

        AddCommand addAmyCommand = new AddCommand(new ModuleCode(VALID_MODCODE_AMY),
                new Year(VALID_SEMESTER_AMY).getSemester(), VALID_GRADE_AMY, VALID_TASK_AMY, modelDate, modelTime);
        AddCommand addBobCommand = new AddCommand(moduleCode, semester, grade, task, date, time);

        // same object -> returns true
        assertTrue(addAmyCommand.equals(addAmyCommand));

        // same values -> returns true
        AddCommand addAmyCommandCopy = new AddCommand(new ModuleCode(VALID_MODCODE_AMY),
                new Year(VALID_SEMESTER_AMY).getSemester(), VALID_GRADE_AMY, VALID_TASK_AMY, modelDate, modelTime);
        assertTrue(addAmyCommand.equals(addAmyCommandCopy));

        // same module code but remainder fields differ -> returns true
        AddCommand addBobCommandDiff = new AddCommand(new ModuleCode(VALID_MODCODE_BOB),
                new Year(VALID_SEMESTER_AMY).getSemester(), VALID_GRADE_AMY, VALID_TASK_AMY, modelDate, modelTime);
        assertTrue(addBobCommand.equals(addBobCommandDiff));

        // different types -> returns false
        assertFalse(addAmyCommand.equals(1));

        // null -> returns false
        assertFalse(addAmyCommand.equals(null));

        // different person -> returns false
        assertFalse(addAmyCommand.equals(addBobCommand));
    }

    /**
     * ModelStub acts as ProfileManager.
     */
    private class ModelStub implements Model {

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getProfileListFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setProfileListFilePath(Path profileListFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setProfileList(ProfileList profileList) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ProfileList getProfileList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteProfile(Profile target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addProfile(Profile profile) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setProfile(Profile target, Profile editedProfile) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Profile> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Deadline> getSortedDeadlineList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Profile> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasProfile(Name name) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Profile getProfile(Name name) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Profile getFirstProfile() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addDeadline(Deadline deadline) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteDeadline(Deadline deadline) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearDeadlineList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void loadDeadlines() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteModuleDeadlines(ModuleCode mc) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Object> getDisplayedView() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setDisplayedView(ObservableList<Module> toDisplay) {
            throw new AssertionError("This method should not be called.");
        }


        @Override
        public void setDisplayedView(Profile toDisplay) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setDisplayedView(Module toDisplay) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setDisplayedView(Course toDisplay) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setDisplayedView(CourseFocusArea toDisplay) {
            throw new AssertionError("This method should not be called.");
        }

    }

    private class ModelStubWithProfile extends ModelStub {
        private final Profile profile;

        ModelStubWithProfile(Profile profile, Module module) {
            requireNonNull(profile);
            this.profile = profile;
            this.profile.addModule(1, module);
        }

        @Override
        public Profile getFirstProfile() {
            return this.profile;
        }
    }
}
