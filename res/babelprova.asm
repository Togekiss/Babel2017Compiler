	.text
	.align	2
	.globl	main
main:
	move $fp, $sp
	li   $t0, 1
	sw   $t0, -0($gp)
	li   $t0, 1
	sw   $t0, -4($gp)
	li   $t0, 1
	sw   $t0, -8($gp)
	li   $t0, 1
	sw   $t0, -28($gp)
	li   $t0, 4
	sw   $t0, -24($gp)
	jr $ra
