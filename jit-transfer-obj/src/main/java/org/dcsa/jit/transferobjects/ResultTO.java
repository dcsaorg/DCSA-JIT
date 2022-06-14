package org.dcsa.jit.transferobjects;

import lombok.Builder;

import java.util.List;

public record ResultTO (
  List<OperationsEventTO> operationsEventTOs,
  int totalPages
) {
    @Builder // workaround for intellij issue
  public ResultTO { }

}
