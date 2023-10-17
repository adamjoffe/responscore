package io.ajo.responscore.service.validation;

import io.ajo.responscore.config.Config;
import io.ajo.responscore.form.Form;
import io.ajo.responscore.service.validation.annotation.ValidForm;


// wrap the config and form together to use validation framework
@ValidForm
public record FormContainer(
        Config config,
        Form form
) {}