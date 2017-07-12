	.text
	.align	2
	.globl	main
	.data
error:
	.asciiz "[ERR_GC_1] Index de vector fora de limits"
	.text
main:
	move $fp, $sp
	.data
E1: .asciiz "ho%&/ajn l·l egfdsa"
	.text
	li   $t0, 1
	sw   $t0, -0($gp)
	li   $t0, 1
	sw   $t0, -4($gp)
	li $t0, 3
	li $t1, 7
	li   $t0, 1
	sw   $t0, -8($gp)
	li   $t0, 1
	sw   $t0, -28($gp)
	li $t1, 1
	li   $t2, 1
	sw   $t2, -4($gp)
	li $t1, 3
	li   $t2, 3
	sw   $t2, -0($gp)
	lw   $t1, -0($gp)
	lw   $t2, -0($gp)
	li   $t3, 3
	blt   $t2, $t3, E2
	li   $t3, 7
	bgt   $t2, $t3, E2
	li $t1, 1
	li   $t3, 3
	li   $t4, 4
	mul   $t3, $t3, $t4
	li   $t4, 8
	subu   $t3, $t4, $t3
	li   $t4, 4
	mul   $t4, $t2, $t4
	addu   $t3, $t4, $t3
	la   $t4, 0($gp)
	subu   $t3, $t4, $t3
	li   $t4, 1
	sw   $t4, 0($t3)
	b	E3
E2:
	li   $v0, 4
	la   $a0, error
	syscall
	li   $v0, 10
	syscall
E3:
	li $t1, 4
	li $t2, 3
	li $t1, 1
	li $t1, 0
	li   $t2, 0
	sw   $t2, -4($gp)
	b	E5
E4:
	li $t1, 9
	li   $t2, 9
	sw   $t2, -0($gp)
E5:
	jr $ra
