package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.*;
import static seedu.address.model.profile.Profile.getModules;

import java.util.stream.Stream;

import seedu.address.logic.commands.ShowCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.CourseManager;
import seedu.address.model.ModuleList;
import seedu.address.model.ModuleManager;
import seedu.address.model.profile.Name;
import seedu.address.model.profile.course.Course;
import seedu.address.model.profile.course.CourseFocusArea;
import seedu.address.model.profile.course.CourseName;
import seedu.address.model.profile.course.module.Module;
import seedu.address.model.profile.course.module.ModuleCode;

/**
 * Parses input arguments and creates a new ShowCommand object
 */
public class ShowCommandParser implements Parser<ShowCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ShowCommand
     * and returns an ShowCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ShowCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_SEMESTER, PREFIX_COURSE_NAME,
                        PREFIX_MODULE, PREFIX_FOCUS_AREA, PREFIX_NAME);

        if (arePrefixesPresent(argMultimap, PREFIX_SEMESTER)) {
            String semester = argMultimap.getValue(PREFIX_SEMESTER).get();
            if (!ParserUtil.isInteger(semester)) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ShowCommand.MESSAGE_USAGE));
            }
            int intSemester = Integer.parseInt(semester);
            ModuleList modulesList = getModules(intSemester);

            return new ShowCommand(modulesList);
        }

        if (arePrefixesPresent(argMultimap, PREFIX_COURSE_NAME)) {
            String name = argMultimap.getValue(PREFIX_COURSE_NAME).get();
            Course course = CourseManager.getCourse(new CourseName(name));
            return new ShowCommand(course);
        }

        if (arePrefixesPresent(argMultimap, PREFIX_MODULE)) {
            String name = argMultimap.getValue(PREFIX_MODULE).get();
            Module module = ModuleManager.getModule(new ModuleCode(name));
            return new ShowCommand(module);
        }

        if (arePrefixesPresent(argMultimap, PREFIX_FOCUS_AREA)) {
            String focusArea = argMultimap.getValue(PREFIX_FOCUS_AREA).get();
            CourseFocusArea courseFocusArea = CourseManager.getCourseFocusArea(focusArea);
            return new ShowCommand(courseFocusArea);
        }

        Name name;
        if (arePrefixesPresent(argMultimap, PREFIX_NAME)) {
            String strName = argMultimap.getValue(PREFIX_NAME).get();
            name = ParserUtil.parseName(strName);
            return new ShowCommand(name);
        }

        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ShowCommand.MESSAGE_USAGE));
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}

