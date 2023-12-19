package express.field.agent.Model;

public class ProfilePerson {

    private String businessUnitId;
    private String buName;
    private String actorId;
    private String frontEndRecordId;
    private String firstName;
    private String lastName;
    private String accountOfficer;
    private String nationalId;
    private String dateOfBirth;
    private String placeOfBirth;
    private String nationality;
    private String gender;
    private Object isEnabled;
    private Object isDeleted;
    private String maritalStatusId;
    private Object age;
    private String middleName;
    private String educationId;
    private String employmentId;
    private String employmentDate;
    private String incomeRangeId;
    private String employerName;
    private String employerCategoryId;
    private String familyMembers;

    public String getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(String businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getFrontEndRecordId() {
        return frontEndRecordId;
    }

    public void setFrontEndRecordId(String frontEndRecordId) {
        this.frontEndRecordId = frontEndRecordId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Object getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Object isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Object getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Object isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(String maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
    }

    public Object getAge() {
        return age;
    }

    public void setAge(Object age) {
        this.age = age;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEducationId() {
        return educationId;
    }

    public void setEducationId(String educationId) {
        this.educationId = educationId;
    }

    public String getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(String employmentId) {
        this.employmentId = employmentId;
    }

    public String getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(String employmentDate) {
        this.employmentDate = employmentDate;
    }

    public String getIncomeRangeId() {
        return incomeRangeId;
    }

    public void setIncomeRangeId(String incomeRangeId) {
        this.incomeRangeId = incomeRangeId;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerCategoryId() {
        return employerCategoryId;
    }

    public void setEmployerCategoryId(String employerCategoryId) {
        this.employerCategoryId = employerCategoryId;
    }

    public String getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(String familyMembers) {
        this.familyMembers = familyMembers;
    }

    public String getAccountOfficer() {
        return accountOfficer;
    }

    public void setAccountOfficer(String accountOfficer) {
        this.accountOfficer = accountOfficer;
    }

    public String getName() {
        return String.format("%s %s %s", firstName, middleName != null ? middleName : "", lastName);
    }

}
