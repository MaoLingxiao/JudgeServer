package com.bean;

public class JudgeServerException extends Exception {
	private static final long serialVersionUID = 1L;
	public class CompileError extends JudgeServerException{

	}
	public class SPJCompileError extends JudgeServerException{

	}
	public class TokenVerificationFailed extends JudgeServerException{

	}
	public class JudgeClientError extends JudgeServerException{

	}
	public class JudgeServiceError extends JudgeServerException{

	}
}

