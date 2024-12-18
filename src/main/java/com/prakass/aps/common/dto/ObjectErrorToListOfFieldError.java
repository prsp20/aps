package com.prakass.aps.common.dto;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ObjectErrorToListOfFieldError {
  public static List<ValidationError> getFieldErrors(BindingResult validationResult) {
    List<FieldError> errorList = validationResult.getFieldErrors();
    List<ValidationError> responseErrorList = new ArrayList<>();
    for (FieldError error : errorList) {
      responseErrorList.add(new ValidationError(error.getField(), error.getDefaultMessage()));
    }
    return responseErrorList;
  }
}
