package validator;

import model.Involvement;

public class InvolvementValidator implements Validator<Long, Involvement> {
    @Override
    public void validate(Involvement entity) {
        String errors = "";
        if (!validateRole(entity))
            errors += ErrorMessages.INVOLVEMENT_INVALID_ROLE;
        if (!validateUser(entity))
            errors += ErrorMessages.INVOLVEMENT_INVALID_USER;
        if (!validateProject(entity))
            errors += ErrorMessages.INVOLVEMENT_INVALID_PROJECT;
        if (!errors.equals(""))
            throw new ValidationException(errors);
    }

    /**
     * Method for verifying if an involvement has an associated role
     * @param involvement: Involvement, the involvement to verify
     * @return true, if the involvement has an associated role
     *         false, otherwise
     */
    private boolean validateRole(Involvement involvement) {
        return involvement.getRole() != null;
    }

    /**
     * Method for verifying if an involvement has an associated user
     * @param involvement: Involvement, the involvement to verify
     * @return true, if the involvement has an associated user
     *         false, otherwise
     */
    private boolean validateUser(Involvement involvement) {
        return involvement.getUser() != null;
    }

    /**
     * Method for verifying if an involvement has an associated project
     * @param involvement: Involvement, the involvement to verify
     * @return true, if the involvement has an associated project
     *         false, otherwise
     */
    private boolean validateProject(Involvement involvement) {
        return involvement.getProject() != null;
    }
}
