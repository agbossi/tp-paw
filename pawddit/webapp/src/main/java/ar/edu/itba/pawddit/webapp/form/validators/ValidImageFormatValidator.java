package ar.edu.itba.pawddit.webapp.form.validators;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.pawddit.webapp.form.ImageForm;
import ar.edu.itba.pawddit.webapp.form.annotations.ValidImageFormat;

public class ValidImageFormatValidator implements ConstraintValidator<ValidImageFormat, ImageForm> {
		
	@Override
	public void initialize(ValidImageFormat constraintAnnotation) {}

	@Override
	public boolean isValid(ImageForm form, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addNode("file").addConstraintViolation();
		
		return isValidFormat(form);
	}
	
	private boolean isValidFormat(ImageForm form) {
		
		if (form.getFileBodyPart() == null || form.getFileBytes() == null)
			return false;
		
		if (form.getFileBytes().length > 5 * 1024 * 1024)
			return false;
			
		boolean valid = false;
		final String[] values = { "image/jpeg", "image/png" };
		String contentType = form.getFileBodyPart().getMediaType().toString();
		for (String mediaType : values) {
			if (mediaType.equals(contentType))
				valid = true;
		}
		
		if (!valid)
			return false;
		
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(form.getFileBytes());
			BufferedImage img = ImageIO.read(in);
			if (img == null)
				return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

}
